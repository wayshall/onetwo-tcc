package org.onetwo.tcc.samples.api;

import javax.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
    
    @Data
    public class ReduceStockRequest {
    	@NotNull
    	Long skuId;
    	@NotNull
    	Integer stockCount;
    }
}

