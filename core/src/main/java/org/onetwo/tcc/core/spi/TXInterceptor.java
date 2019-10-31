package org.onetwo.tcc.core.spi;

import java.lang.reflect.Method;
import java.util.Collection;

import org.onetwo.common.interceptor.Interceptor;
import org.onetwo.common.interceptor.InterceptorChain;
import org.onetwo.common.interceptor.SimpleInterceptorChain;
import org.onetwo.tcc.core.internal.TransactionResourceHolder;

/**
 * @author weishao zeng
 * <br/>
 */
public interface TXInterceptor extends Interceptor {
	
	@Override
	default Object intercept(InterceptorChain chain) {
		return handleIntercept((TXInterceptorChain)chain);
	}
	
	Object handleIntercept(TXInterceptorChain chain);
	
	public class TXInterceptorChain extends SimpleInterceptorChain<TXInterceptor> {

		private TransactionResourceHolder resourceHolder;
		
		public TXInterceptorChain(TransactionResourceHolder resourceHolder, 
				Object targetObject, Method targetMethod,
				Object[] targetArgs, Collection<TXInterceptor> interceptors, ActualInvoker actualInvoker) {
			super(targetObject, targetMethod, targetArgs, interceptors, actualInvoker);
			this.resourceHolder = resourceHolder;
		}

		public TransactionResourceHolder getResourceHolder() {
			return resourceHolder;
		}
		
		
	}

}

