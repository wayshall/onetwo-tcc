package org.onetwo.tcc.samples.order.client;

import org.onetwo.tcc.samples.api.SkuApi;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * @author weishao zeng
 * <br/>
 */
@FeignClient(name="skuClient", url="http://localhost:9082")
public interface SkuClient extends SkuApi {

}

