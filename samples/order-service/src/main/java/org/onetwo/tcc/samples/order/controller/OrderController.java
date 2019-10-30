

package org.onetwo.tcc.samples.order.controller;

import org.onetwo.boot.core.web.controller.DateInitBinder;
import org.onetwo.tcc.samples.order.entity.OrderInfoEntity;
import org.onetwo.tcc.samples.order.service.impl.OrderInfoServiceImpl;
import org.onetwo.tcc.samples.order.vo.CreateOrderRequest;
import org.onetwo.tcc.samples.order.vo.CreateOrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController implements DateInitBinder {

    @Autowired
    private OrderInfoServiceImpl orderInfoService;
    
    
    @RequestMapping(method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public CreateOrderResponse create(@RequestBody CreateOrderRequest request){
    	OrderInfoEntity order = this.orderInfoService.save(request);
    	return CreateOrderResponse.builder()
    							.id(order.getId())
    							.build();
    }
}