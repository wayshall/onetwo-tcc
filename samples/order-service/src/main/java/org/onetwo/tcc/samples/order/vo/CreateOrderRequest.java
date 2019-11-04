package org.onetwo.tcc.samples.order.vo;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author weishao zeng
 * <br/>
 */
@Data
public class CreateOrderRequest {
	@NotNull
	Long skuId;
	
	@NotNull
	Integer count;

	Long couponId;
	
	int sleepInSecondsOnReduceStock;
	
}

