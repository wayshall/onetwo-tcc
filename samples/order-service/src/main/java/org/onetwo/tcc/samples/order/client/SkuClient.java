package org.onetwo.tcc.samples.order.client;

import org.onetwo.tcc.samples.api.SkuApi;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * @author weishao zeng
 * <br/>
 */
@FeignClient(name="skuClient", url=SkuClient.SERVICE_URL)
public interface SkuClient extends SkuApi {
	
	String SERVICE_URL = "http://localhost:9082";

}

