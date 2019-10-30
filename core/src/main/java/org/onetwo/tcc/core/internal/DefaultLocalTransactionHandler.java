package org.onetwo.tcc.core.internal;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.onetwo.common.reflect.ReflectUtils;
import org.onetwo.common.spring.SpringUtils;
import org.onetwo.common.utils.LangUtils;
import org.onetwo.common.utils.StringUtils;
import org.onetwo.tcc.core.entity.TXLogEntity;
import org.onetwo.tcc.core.entity.TXLogEntity.TXContentData;
import org.onetwo.tcc.core.exception.TCCErrors;
import org.onetwo.tcc.core.exception.TCCException;
import org.onetwo.tcc.core.internal.message.GTXLogMessage;
import org.onetwo.tcc.core.spi.LocalTransactionHandler;
import org.onetwo.tcc.core.spi.TXLogRepository;
import org.onetwo.tcc.core.util.GTXActions;
import org.onetwo.tcc.core.util.TCCTransactionType;
import org.onetwo.tcc.core.util.TXStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.util.ReflectionUtils.MethodFilter;

import lombok.extern.slf4j.Slf4j;

/**
 * @author weishao zeng
 * <br/>
 */
@Slf4j
public class DefaultLocalTransactionHandler implements LocalTransactionHandler {

	@Autowired
	private TXLogRepository txlogRepository;
	@Autowired
	private ApplicationContext applicationContext;
	
	@Override
	public void handle(GTXLogMessage txlogMessage) {
		if (log.isInfoEnabled()) {
			log.info("received GTXLog message： {}", txlogMessage);
		}
		if (txlogMessage.getAction()==null) {
			log.info("GTXLog message was ignored for txid： {}", txlogMessage.getId());
			return ;
		}
		
		List<TXLogEntity> txlogs = txlogRepository.findListByGTXId(txlogMessage.getId());
		if (LangUtils.isEmpty(txlogs)) {
			if (log.isInfoEnabled()) {
				log.info("TXLog message not found for txid： {}", txlogMessage.getId());
			}
			return ;
		}
		
		for (TXLogEntity txlog : txlogs) {
			if (log.isInfoEnabled()) {
				log.info(txlog.logMessage(" handle transational for txlog ： {}"), txlog);
			}
			if (txlogMessage.getAction()==GTXActions.COMMIT) {
				handleGTXCommit(txlog);
			} else if (txlogMessage.getAction()==GTXActions.ROLLBACK) {
				handleGTXRollback(txlog);
			} else {
				throw new UnsupportedOperationException("unknow tx action: " + txlogMessage.getAction());
			}
		}
	}
	
	protected void handleGTXCommit(TXLogEntity txlog) {
		String beanType = txlog.getContent().getTargetClass();
		TXStatus txStatus = txlog.getStatus();
		switch (txStatus) {
		case COMMITED:
			invokeConfirm(txlog);
			break;
		case EXECUTING:
			handleExecuting(txlog);
			break;
		case ROLLBACKED:
			log.warn(txlog.logMessage(" status is {}. try method[{}] is rollbacked but global transaction was committed. "
					+ "it maybe catch exception of try method on the client service"),
					txStatus,
					beanType + "#" + txlog.getContent().getTryMethod());
			break;

		default:
			log.warn(txlog.logMessage(" status is {}. global transaction was committed, it maybe occur error."), txStatus);
			break;
		}
		handleCompleted(txlog);
	}
	
	protected void handleGTXRollback(TXLogEntity txlog) {
		String beanType = txlog.getContent().getTargetClass();
		TXStatus txStatus = txlog.getStatus();
		switch (txStatus) {
		case COMMITED:
			invokeCancel(txlog);
			break;
		case EXECUTING:
			handleExecuting(txlog);
			break;
		case ROLLBACKED:
			log.info(txlog.logMessage(" status is {}. ignore invoke cancel method of {}"),
					txStatus,
					beanType + "#" + txlog.getContent().getTryMethod());
			break;

		default:
			log.warn(txlog.logMessage(" status is {}. global transaction was committed, it maybe occur error."), txStatus);
			break;
		}
		handleCompleted(txlog);
	}
	
	private void handleExecuting(TXLogEntity txlog) {
		String beanType = txlog.getContent().getTargetClass();
		TXStatus txStatus = txlog.getStatus();
		log.warn(txlog.logMessage("status is {}. try method[{}] is executing but global transaction was committed. "
				+ "it maybe try method run too long to timeout, and client service ignored timeout"),
				txlog.getId(),
				txStatus,
				beanType + "#" + txlog.getContent().getTryMethod());
		txlog.setStatus(TXStatus.RB_ONLY);
	}
	
	protected void handleCompleted(TXLogEntity txlog) {
		txlogRepository.completed(txlog);
		log.info(txlog.logMessage(" has completed: {}"), txlog);
	}
	
	protected void invokeConfirm(TXLogEntity txlog) {
		log.info(txlog.logMessage("status is {}. try to invoke confirm method..."), txlog.getId(), txlog.getStatus());
		this.invokeTccMethod(txlog, GTXActions.COMMIT);
		txlog.setStatus(TXStatus.CONFIRMED);
	}

	protected void invokeCancel(TXLogEntity txlog) {
		log.info(txlog.logMessage("status is {}. try to invoke cancel method..."), txlog.getId(), txlog.getStatus());
		this.invokeTccMethod(txlog, GTXActions.ROLLBACK);
		txlog.setStatus(TXStatus.CANCELED);
	}
	
	/***
	 * invoke confirm or cancel by TXActions
	 * @author weishao zeng
	 * @param txlog
	 * @param txAction
	 */
	protected void invokeTccMethod(TXLogEntity txlog, GTXActions txAction) {
		String methodName = null;
		TXContentData data = txlog.getContent();
		TCCErrors error = null;
		String tag = null;
		if (txAction==GTXActions.COMMIT) {
			methodName = data.getConfirmMethod();
			error = TCCErrors.ERR_TOO_MANY_CONFIRM;
			tag = "confirm";
		} else if (txAction==GTXActions.ROLLBACK) {
			methodName = data.getCancelMethod();
			error = TCCErrors.ERR_TOO_MANY_CANCEL;
			tag = "cancel";
		} else {
			throw new UnsupportedOperationException("unknow tx actions: " + txAction);
		}
		if (StringUtils.isBlank(methodName) && txlog.getTransactionType()==TCCTransactionType.GLOBAL) {
			if (log.isWarnEnabled()) {
				log.warn(txlog.logMessage(" there is not {} method bound the global transacton!"), tag);
			}
			return ;
		}
		
		Class<?> beanType = ReflectUtils.loadClass(data.getTargetClass());
		Object bean = SpringUtils.getBean(applicationContext, beanType);
		String targetMethod = methodName;
		Set<Method> methods = MethodIntrospector.selectMethods(beanType, (MethodFilter)method -> {
			return method.getName().equals(targetMethod) && method.getParameters().length==data.getArguments().length;
		});
		if (methods.size()!=1) {
			throw new TCCException(error)
								.put("match methods", methods)
								.put("target class", beanType);
		}
		Method method = LangUtils.getFirst(methods);
		ReflectUtils.invokeMethod(method, bean, data.getArguments());
	}
}

