package org.onetwo.tcc.core.internal;

import java.util.Collection;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.onetwo.tcc.core.spi.TXInterceptor;
import org.onetwo.tcc.core.spi.TXInterceptor.TXInterceptorChain;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 把tcc的事务同步器放到本地事务上下文
 * @author weishao zeng
 * <br/>
 */
//@Service
public class TccAndLocalSynchronizationWrapper {
	
	@Transactional
	public Object wrap(ProceedingJoinPoint pjp, TransactionResourceHolder resource, Collection<TXInterceptor> interceptors, boolean mustRegisterSynchronization) {
//		if (!TransactionSynchronizationManager.isSynchronizationActive()) {
//			TransactionSynchronizationManager.initSynchronization();
//		}
		if (mustRegisterSynchronization) {
			TCCTransactionSynchronization synchronization = new TCCTransactionSynchronization(resource);
			TransactionSynchronizationManager.registerSynchronization(synchronization);
		}
		
		MethodSignature ms = (MethodSignature)pjp.getSignature();
		TXInterceptorChain interceptorChain = new TXInterceptorChain(resource, 
						pjp.getTarget(), 
						ms.getMethod(), 
						pjp.getArgs(), 
						interceptors, 
						() -> {
							return pjp.proceed();
						});
		Object result = interceptorChain.invoke();
		return result;
	}

}
