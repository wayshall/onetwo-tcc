package org.onetwo.tcc.samples.order.client;

import org.onetwo.tcc.samples.usr.api.CouponApi;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * @author weishao zeng
 * <br/>
 */
@FeignClient(name="couponClient", url=CouponClient.SERVICE_URL)
public interface CouponClient extends CouponApi {
	
	String SERVICE_URL = "http://localhost:9083";

}

