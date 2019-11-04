package org.onetwo.tcc.samples.product.api;

import javax.validation.constraints.NotNull;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

/**
 * @author weishao zeng
 * <br/>
 */
@Api(tags = "sku Api")
@RequestMapping("/sku")
public interface SkuApi {

    @ApiOperation(value = "扣减库存")
    @PostMapping("/reduceStock")
    void reduceStock(@RequestBody ReduceStockRequest request);
    
    @PostMapping(value="/cleanAndCreateSku", consumes=MediaType.APPLICATION_JSON_UTF8_VALUE)
    SkuVO cleanAndCreateSku(@RequestBody SkuVO request);
    
    @GetMapping("get")
    SkuVO get(@RequestParam("skuId") Long skuId);
    
    @Data
    public class ReduceStockRequest {
    	@NotNull
    	Long skuId;
    	@NotNull
    	Integer stockCount;
    	int sleepInSeconds;
    }
    
    
    @Data
    public class SkuVO {
    	Long id;
    	String name;
    	Integer stockCount;
        Integer frozenStockCount;
    }
}

