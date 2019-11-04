

package org.onetwo.tcc.samples.order.controller;

import org.onetwo.boot.core.web.controller.DateInitBinder;
import org.onetwo.tcc.samples.order.entity.OrderInfoEntity;
import org.onetwo.tcc.samples.order.service.impl.OrderInfoServiceImpl;
import org.onetwo.tcc.samples.order.vo.CreateOrderRequest;
import org.onetwo.tcc.samples.order.vo.CreateOrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController implements DateInitBinder {

    @Autowired
    private OrderInfoServiceImpl orderInfoService;
    
    
    @PostMapping(value="createSuccess", consumes=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public CreateOrderResponse createSuccess(@RequestBody CreateOrderRequest request){
    	OrderInfoEntity order = this.orderInfoService.createSuccess(request);
    	return CreateOrderResponse.builder()
    							.id(order.getId().toString())
    							.build();
    }
    
    @PostMapping(value="failAfterReduceStock", consumes=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public CreateOrderResponse failAfterReduceStock(@RequestBody CreateOrderRequest request){
    	OrderInfoEntity order = this.orderInfoService.failAfterReduceStock(request);
    	return CreateOrderResponse.builder()
    							.id(order.getId().toString())
    							.build();
    }
    
    @PostMapping(value="createWithCreatingSuccess", consumes=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public CreateOrderResponse createWithCreatingSuccess(@RequestBody CreateOrderRequest request){
    	OrderInfoEntity order = this.orderInfoService.createWithCreatingSuccess(request);
    	return CreateOrderResponse.builder()
    							.id(order.getId().toString())
    							.build();
    }
}