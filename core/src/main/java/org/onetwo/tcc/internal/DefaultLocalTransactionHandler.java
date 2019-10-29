package org.onetwo.tcc.internal;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.onetwo.common.reflect.ReflectUtils;
import org.onetwo.common.spring.SpringUtils;
import org.onetwo.common.utils.LangUtils;
import org.onetwo.tcc.entity.TXLogEntity;
import org.onetwo.tcc.entity.TXLogEntity.TXContentData;
import org.onetwo.tcc.exception.TCCErrors;
import org.onetwo.tcc.exception.TCCException;
import org.onetwo.tcc.internal.message.TXLogMessage;
import org.onetwo.tcc.spi.LocalTransactionHandler;
import org.onetwo.tcc.spi.TXLogRepository;
import org.onetwo.tcc.util.TXActions;
import org.onetwo.tcc.util.TXStatus;
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
	public void handle(TXLogMessage txlogMessage) {
		if (txlogMessage.getAction()==null) {
			log.info("TXLog message was ignored for txid： {}", txlogMessage.getId());
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
			if (txlogMessage.getAction()==TXActions.COMMIT) {
				handleGTXCommit(txlog);
			} else if (txlogMessage.getAction()==TXActions.ROLLBACK) {
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
		markCompleted(txlog);
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
		markCompleted(txlog);
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
	
	protected void markCompleted(TXLogEntity txlog) {
		txlogRepository.updateToCompleted(txlog);
		log.info("TXLog has completed: {}", txlog);
	}
	
	protected void invokeConfirm(TXLogEntity txlog) {
		log.info(txlog.logMessage("status is {}. try to invoke confirm method..."), txlog.getId(), txlog.getStatus());
		this.invokeTccMethod(txlog, TXActions.COMMIT);
		txlog.setStatus(TXStatus.CONFIRMED);
	}

	protected void invokeCancel(TXLogEntity txlog) {
		log.info(txlog.logMessage("status is {}. try to invoke cancel method..."), txlog.getId(), txlog.getStatus());
		this.invokeTccMethod(txlog, TXActions.ROLLBACK);
		txlog.setStatus(TXStatus.CANCELED);
	}
	
	/***
	 * invoke confirm or cancel by TXActions
	 * @author weishao zeng
	 * @param txlog
	 * @param txAction
	 */
	protected void invokeTccMethod(TXLogEntity txlog, TXActions txAction) {
		String methodName = null;
		TXContentData data = txlog.getContent();
		TCCErrors error = null;
		if (txAction==TXActions.COMMIT) {
			methodName = data.getConfirmMethod();
			error = TCCErrors.ERR_TOO_MANY_CONFIRM;
		} else if (txAction==TXActions.ROLLBACK) {
			methodName = data.getCancelMethod();
			error = TCCErrors.ERR_TOO_MANY_CANCEL;
		} else {
			throw new UnsupportedOperationException("unknow tx actions: " + txAction);
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

