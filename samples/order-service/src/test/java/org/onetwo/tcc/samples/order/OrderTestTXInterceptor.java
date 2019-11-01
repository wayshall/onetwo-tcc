package org.onetwo.tcc.samples.order;

import java.util.Map;

import org.onetwo.tcc.core.spi.TXInterceptor;
import org.onetwo.tcc.samples.order.vo.CreateOrderRequest;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

/**
 * @author weishao zeng
 * <br/>
 */
@Component
public class OrderTestTXInterceptor implements TXInterceptor {
	/***
	 * just for test
	 */
	public static Map<Long, String> SkuGtxIdMap = Maps.newHashMap();

	@Override
	public Object handleIntercept(TXInterceptorChain chain) {
		if (chain.getResourceHolder().isGlobalTX()) {
			if (chain.getTargetArgs()[0] instanceof CreateOrderRequest) {
				CreateOrderRequest orderReq = (CreateOrderRequest) chain.getTargetArgs()[0];
				SkuGtxIdMap.put(orderReq.getSkuId(), chain.getResourceHolder().getGtxId());
			}
		}
		return chain.invoke();
	}

}

