
package org.onetwo.tcc.samples.order.service.impl;

import org.onetwo.common.db.spi.BaseEntityManager;
import org.onetwo.common.exception.ServiceException;
import org.onetwo.tcc.core.annotation.TCCService;
import org.onetwo.tcc.core.annotation.TCCTransactional;
import org.onetwo.tcc.samples.order.client.CouponClient;
import org.onetwo.tcc.samples.order.client.SkuClient;
import org.onetwo.tcc.samples.order.entity.OrderInfoEntity;
import org.onetwo.tcc.samples.order.entity.OrderInfoEntity.OrderStatus;
import org.onetwo.tcc.samples.order.vo.CreateOrderRequest;
import org.onetwo.tcc.samples.product.api.SkuApi.ReduceStockRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@TCCService
public class OrderInfoServiceImpl {

    @Autowired
    private BaseEntityManager baseEntityManager;
    
    @Autowired
    private SkuClient skuClient;
    @Autowired
    private CouponClient couponClient;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private OrderInfoServiceImpl orderInfoService;
    
    @TCCTransactional(globalized=true)
    @Transactional
    public OrderInfoEntity createSuccess(CreateOrderRequest request) {
    	OrderInfoEntity order = new OrderInfoEntity();
    	order.setSkuId(request.getSkuId());
    	order.setSkuCount(request.getCount());
    	order.setStatus(OrderStatus.CREATED);
    	order.setPrice(10.0D*request.getCount());
    	order.setTitle("测试订单-createSuccess");
		baseEntityManager.persist(order);
		
		ReduceStockRequest stockRequest = new ReduceStockRequest();
		stockRequest.setSkuId(request.getSkuId());
		stockRequest.setStockCount(request.getCount());
		stockRequest.setSleepInSeconds(request.getSleepInSecondsOnReduceStock());
		skuClient.reduceStock(stockRequest);
		
		if (request.getCouponId()!=null) {
			couponClient.frozonCoupon(request.getCouponId());
		}
		
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
//		skuClient.reduceStock(stockRequest);
		HttpEntity res = restTemplate.postForEntity(SkuClient.SERVICE_URL + "/sku/reduceStock", stockRequest, HttpEntity.class);
		

		
		if (request.getCouponId()!=null) {
			couponClient.frozonCoupon(request.getCouponId());
		}
		
		if (true) {
			throw new ServiceException("failAfterReduceStock");
		}
		
		return order;
	}

    @Transactional
    public OrderInfoEntity createWithCreatingSuccess(CreateOrderRequest request) {
    	OrderInfoEntity order = new OrderInfoEntity();
    	order.setSkuId(request.getSkuId());
    	order.setSkuCount(request.getCount());
    	order.setStatus(OrderStatus.CREATING);
    	order.setPrice(10.0D*request.getCount());
    	order.setTitle("测试订单-createSuccess2");
		baseEntityManager.persist(order);
		
		orderInfoService.creatingOrder(request, order);
		
		return order;
		
    }
    
    @TCCTransactional(globalized=true, confirmMethod="confirmOrder", cancelMethod="cancelOrder")
    public OrderInfoEntity creatingOrder(CreateOrderRequest request, OrderInfoEntity order) {
		ReduceStockRequest stockRequest = new ReduceStockRequest();
		stockRequest.setSkuId(request.getSkuId());
		stockRequest.setStockCount(request.getCount());
		stockRequest.setSleepInSeconds(request.getSleepInSecondsOnReduceStock());
		skuClient.reduceStock(stockRequest);
		
		if (request.getCouponId()!=null) {
			couponClient.frozonCoupon(request.getCouponId());
		}
		
		return order;
	}
    
    protected void confirmOrder(CreateOrderRequest request, OrderInfoEntity order) {
    	order.setStatus(OrderStatus.CREATED);
    	baseEntityManager.update(order);
    }
    
    protected void cancelOrder(CreateOrderRequest request, OrderInfoEntity order) {
    	order.setStatus(OrderStatus.CANCEL);
    	baseEntityManager.update(order);
    }

}