package org.onetwo.tcc.samples.order;

import org.onetwo.tcc.core.spi.TXInterceptor;
import org.springframework.stereotype.Component;

/**
 * @author weishao zeng
 * <br/>
 */
@Component
public class OrderTestTXInterceptor implements TXInterceptor {
	
	public static String gtxid = "";

	@Override
	public Object handleIntercept(TXInterceptorChain chain) {
		gtxid = chain.getResourceHolder().getGtxId();
		return chain.invoke();
	}

}

