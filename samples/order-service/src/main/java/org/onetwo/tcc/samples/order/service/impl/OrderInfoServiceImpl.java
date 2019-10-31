
package org.onetwo.tcc.samples.order.service.impl;

import org.onetwo.common.db.spi.BaseEntityManager;
import org.onetwo.common.exception.ServiceException;
import org.onetwo.tcc.core.annotation.TCCService;
import org.onetwo.tcc.core.annotation.TCCTransactional;
import org.onetwo.tcc.samples.api.SkuApi.ReduceStockRequest;
import org.onetwo.tcc.samples.order.client.SkuClient;
import org.onetwo.tcc.samples.order.entity.OrderInfoEntity;
import org.onetwo.tcc.samples.order.entity.OrderInfoEntity.OrderStatus;
import org.onetwo.tcc.samples.order.vo.CreateOrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@TCCService
public class OrderInfoServiceImpl {

    @Autowired
    private BaseEntityManager baseEntityManager;
    @Autowired
    private SkuClient skuClient;
    
    @TCCTransactional(globalized=true)
    @Transactional
    public OrderInfoEntity save(CreateOrderRequest request) {
    	OrderInfoEntity order = new OrderInfoEntity();
    	order.setSkuId(request.getSkuId());
    	order.setSkuCount(request.getCount());
    	order.setStatus(OrderStatus.CREATED);
    	order.setPrice(10.0D*request.getCount());
    	order.setTitle("测试订单");
		baseEntityManager.persist(order);
		
		ReduceStockRequest stockRequest = new ReduceStockRequest();
		stockRequest.setSkuId(request.getSkuId());
		stockRequest.setStockCount(request.getCount());
		skuClient.reduceStock(stockRequest);
		
		return order;
	}
    
    @TCCTransactional(globalized=true)
    @Transactional
    public OrderInfoEntity failAfterReduceStock(CreateOrderRequest request) {
    	OrderInfoEntity order = new OrderInfoEntity();
    	order.setSkuId(request.getSkuId());
    	order.setSkuCount(request.getCount());
    	order.setStatus(OrderStatus.CREATED);
    	order.setPrice(10.0D*request.getCount());
    	order.setTitle("测试订单-failAfterReduceStock");
		baseEntityManager.persist(order);
		
		ReduceStockRequest stockRequest = new ReduceStockRequest();
		stockRequest.setSkuId(request.getSkuId());
		stockRequest.setStockCount(request.getCount());
		skuClient.reduceStock(stockRequest);
		
		if (true) {
			throw new ServiceException("failAfterReduceStock");
		}
		
		return order;
	}

}