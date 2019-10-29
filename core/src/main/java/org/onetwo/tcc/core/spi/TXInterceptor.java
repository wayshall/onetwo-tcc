package org.onetwo.tcc.core.spi;

import org.onetwo.common.interceptor.Interceptor;
import org.onetwo.common.interceptor.InterceptorChain;

/**
 * @author weishao zeng
 * <br/>
 */
public interface TXInterceptor extends Interceptor {
	
	Object intercept(InterceptorChain chain);

}

