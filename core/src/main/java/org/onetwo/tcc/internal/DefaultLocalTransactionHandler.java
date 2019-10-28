package org.onetwo.tcc.internal;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.onetwo.common.reflect.ReflectUtils;
import org.onetwo.common.spring.SpringUtils;
import org.onetwo.common.utils.LangUtils;
import org.onetwo.tcc.entity.TXLogEntity;
import org.onetwo.tcc.entity.TXLogEntity.TXContentData;
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
		List<TXLogEntity> txlogs = txlogRepository.findListById(txlogMessage.getId());
		if (LangUtils.isEmpty(txlogs)) {
			if (log.isInfoEnabled()) {
				log.info("txlog message not found for txid： {}", txlogMessage.getId());
			}
			return ;
		}
		
		for (TXLogEntity txlog : txlogs) {
			if (log.isInfoEnabled()) {
				log.info("handle transational for txlog ： {}", txlog);
			}
			if (txlogMessage.getAction()==TXActions.COMMIT) {
				commit(txlog);
			} else {
				rollback(txlog);
			}
		}
	}
	
	protected void commit(TXLogEntity txlog) {
		TXStatus txStatus = txlog.getStatus();
		switch (txStatus) {
		case COMMITED:
			invokeConfirm(txlog);
			break;

		default:
			break;
		}
	}
	
	protected void invokeConfirm(TXLogEntity txlog) {
		TXContentData data = txlog.getContent();
		Class<?> beanType = ReflectUtils.loadClass(data.getTargetClass());
		Object bean = SpringUtils.getBean(applicationContext, beanType);
		Set<Method> confirmMethods = MethodIntrospector.selectMethods(beanType, (MethodFilter)method -> {
			return method.getName().equals(data.getConfirmMethod()) && method.getParameters().length==data.getArguments().length;
		});
		if (confirmMethods.size()!=1) {
			throw new TCCException("find too many confirm method: " + confirmMethods);
		}
		Method confirmMethod = LangUtils.getFirst(confirmMethods);
		ReflectUtils.invokeMethod(confirmMethod, bean, data.getArguments());
	}

	protected void rollback(TXLogEntity txlog) {
	}
}

