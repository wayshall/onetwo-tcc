package org.onetwo.tcc.samples.order;
/**
 * @author weishao zeng
 * <br/>
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;
import org.onetwo.common.exception.ServiceException;
import org.onetwo.common.utils.LangUtils;
import org.onetwo.tcc.core.internal.message.TXLogMessage;
import org.onetwo.tcc.core.util.TXStatus;
import org.onetwo.tcc.samples.order.client.CouponClient;
import org.onetwo.tcc.samples.order.client.SkuClient;
import org.onetwo.tcc.samples.order.vo.CreateOrderRequest;
import org.onetwo.tcc.samples.product.api.SkuApi.SkuVO;
import org.onetwo.tcc.samples.usr.api.CouponApi.CouponStatus;
import org.onetwo.tcc.samples.usr.api.CouponApi.CouponVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Commit;

@Commit
public class TccOrderServiceITest extends TccOrderBaseApplicationUTests {

    @Autowired
    private SkuClient skuClient;
    @Autowired
    private CouponClient couponClient;
    
    private boolean testWithCoupon = true;
    
    @Autowired
    private ErrorOnRollbackListenner errorOnRollbackListenner;
//    private SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(1);
//    public static Long skuId;
    
    @Before
    public void setup() {
    }
    
    
	@Test
	public void testCreateOrderSuccess() throws Exception {
		SkuVO createSku = new SkuVO();
		createSku.setStockCount(100);
//		createSku.setId(skuId);
		createSku.setName("韭菜");
		Long skuId = skuClient.cleanAndCreateSku(createSku).getId();
		
		CreateOrderRequest request = new CreateOrderRequest();
		request.setSkuId(skuId);
		request.setCount(1);
		
		if (testWithCoupon) {
			CouponVO coupon = couponClient.clearAndInsertCoupon(1L);
			request.setCouponId(coupon.getId());
		}
		
		String json = createOrder("/order/createSuccess", request);
		System.out.println("json: " + json);
//		CreateOrderResponse res = jsonMapper.fromJson(json, CreateOrderResponse.class);
		
		TXLogMessage skuLog = null;
		while((skuLog = findSkuServiceTXLog(skuId))==null) {
			LangUtils.await(1);
			System.out.println("wait 1 seconds...");
		}
		assertThat(skuLog.getStatus()).isEqualTo(TXStatus.CONFIRMED);
		SkuVO sku = skuClient.get(skuId);
		assertThat(sku).isNotNull();
		assertThat(sku.getFrozenStockCount()).isEqualTo(0);
		assertThat(sku.getStockCount()).isEqualTo(createSku.getStockCount()-request.getCount());
		
		if (testWithCoupon) {
			CouponVO coupon = this.couponClient.get(request.getCouponId());
			assertThat(coupon).isNotNull();
			assertThat(coupon.getStatus()).isEqualTo(CouponStatus.USED);
		}
	}
	

	@Test
	public void testTimeoutOnReduceStock() throws Exception {
		SkuVO createSku = new SkuVO();
		createSku.setStockCount(100);
//		createSku.setId(skuId);
		createSku.setName("韭菜");
		Long skuId = skuClient.cleanAndCreateSku(createSku).getId();
		
		CreateOrderRequest request = new CreateOrderRequest();
		request.setSkuId(skuId);
		request.setCount(1);
		// 库存休眠30秒
		request.setSleepInSecondsOnReduceStock(30);
		
		if (testWithCoupon) {
			CouponVO coupon = couponClient.clearAndInsertCoupon(1L);
			request.setCouponId(coupon.getId());
		}
		
		assertThatThrownBy(() -> {
			String json = createOrder("/order/createSuccess", request);
			System.out.println("json: " + json);
		}).hasRootCauseInstanceOf(TimeoutException.class);
//		CreateOrderResponse res = jsonMapper.fromJson(json, CreateOrderResponse.class);
		
		TXLogMessage skuLog = null;
		while((skuLog = findSkuServiceTXLog(skuId))==null) {
			LangUtils.await(1);
			System.out.println("wait 1 seconds...");
		}


		assertThat(skuLog.getStatus()).isEqualTo(TXStatus.RB_ONLY);
		SkuVO sku = skuClient.get(skuId);
		assertThat(sku).isNotNull();
		assertThat(sku.getFrozenStockCount()).isEqualTo(0);
		assertThat(sku.getStockCount()).isEqualTo(createSku.getStockCount());
		
		if (testWithCoupon) {
			CouponVO coupon = this.couponClient.get(request.getCouponId());
			assertThat(coupon).isNotNull();
			assertThat(coupon.getStatus()).isEqualTo(CouponStatus.VALID);
		}
		
	}

	@Test
	public void testFailAfterReduceStock() throws Exception {
		SkuVO createSku = new SkuVO();
		createSku.setStockCount(100);
//		createSku.setId(skuId);
		createSku.setName("韭菜");
		Long skuId = skuClient.cleanAndCreateSku(createSku).getId();

		CreateOrderRequest request = new CreateOrderRequest();
		request.setSkuId(skuId);
		request.setCount(1);
		
		if (testWithCoupon) {
			CouponVO coupon = couponClient.clearAndInsertCoupon(1L);
			request.setCouponId(coupon.getId());
		}
		
		assertThatThrownBy(() -> {
			String json = createOrder("/order/failAfterReduceStock", request);
			System.out.println("json: " + json);
		}).hasRootCauseInstanceOf(ServiceException.class);
//		CreateOrderResponse res = jsonMapper.fromJson(json, CreateOrderResponse.class);
		
		TXLogMessage skuLog = null;
		while((skuLog = findSkuServiceTXLog(skuId))==null) {
			LangUtils.await(1);
			System.out.println("wait 1 seconds...");
		}
		assertThat(skuLog.getStatus()).isEqualTo(TXStatus.CANCELED);
		SkuVO sku = skuClient.get(skuId);
		assertThat(sku).isNotNull();
		assertThat(sku.getFrozenStockCount()).isEqualTo(0);
		assertThat(sku.getStockCount()).isEqualTo(createSku.getStockCount());
		
		if (testWithCoupon) {
			CouponVO coupon = this.couponClient.get(request.getCouponId());
			assertThat(coupon).isNotNull();
			assertThat(coupon.getStatus()).isEqualTo(CouponStatus.VALID);
		}
	}
	
	/****
	 * 测试标记回滚的时候发生异常时，补偿服务是否正确运行
	 * @author weishao zeng
	 * @throws Exception
	 */
	@Test
	public void testFailAfterReduceStockAndErrorOnRollback() throws Exception {
		SkuVO createSku = new SkuVO();
		createSku.setStockCount(100);
		createSku.setName("韭菜");
		Long skuId = skuClient.cleanAndCreateSku(createSku).getId();
		
		CreateOrderRequest request = new CreateOrderRequest();
		request.setSkuId(skuId);
		request.setCount(1);
		
		if (testWithCoupon) {
			CouponVO coupon = couponClient.clearAndInsertCoupon(1L);
			request.setCouponId(coupon.getId());
		}
		
		this.errorOnRollbackListenner.enabled = true;
		assertThatThrownBy(() -> {
			String json = createOrder("/order/failAfterReduceStock", request);
			System.out.println("json: " + json);
		}).hasRootCauseInstanceOf(ServiceException.class);
//		CreateOrderResponse res = jsonMapper.fromJson(json, CreateOrderResponse.class);

		this.errorOnRollbackListenner.enabled = false;
		TXLogMessage skuLog = null;
		int waitCount = 1;
		while((skuLog = findSkuServiceTXLog(skuId))==null) {
			LangUtils.await(1);
			System.out.println("testFailAfterReduceStockAndErrorOnRollback wait "+waitCount+" seconds...");
			waitCount++;
		}
		assertThat(skuLog.getStatus()).isEqualTo(TXStatus.CANCELED);
		SkuVO sku = skuClient.get(skuId);
		assertThat(sku).isNotNull();
		assertThat(sku.getFrozenStockCount()).isEqualTo(0);
		assertThat(sku.getStockCount()).isEqualTo(createSku.getStockCount());
		
		if (testWithCoupon) {
			CouponVO coupon = this.couponClient.get(request.getCouponId());
			assertThat(coupon).isNotNull();
			assertThat(coupon.getStatus()).isEqualTo(CouponStatus.VALID);
		}
		
	}
	
	private TXLogMessage findSkuServiceTXLog(Long skuId) {
		TXLogMessage txlog = TXLogTestConsumer.getTXLogMessageList().stream().filter(log -> {
			return log.getServiceId().equals("tcc-product-service") && 
					log.isCompleted() && log.getGlobalId().equals(OrderTestTXInterceptor.SkuGtxIdMap.get(skuId));
		}).findFirst().orElse(null);
		return txlog;
	}
	
	private String createOrder(String path, CreateOrderRequest request) throws Exception {
		String json = perform(post(path).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
											.accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
											.content(jsonMapper.toJson(request))
							)
							.andExpect(status().isOk())
							.andExpect(jsonPath("$.id").isNotEmpty() )
							.andReturn().getResponse().getContentAsString();
		return json;
	}

	@Test
	public void testCreateWithCreatingSuccess() throws Exception {
		SkuVO createSku = new SkuVO();
		createSku.setStockCount(100);
//		createSku.setId(skuId);
		createSku.setName("韭菜");
		Long skuId = skuClient.cleanAndCreateSku(createSku).getId();
		
		CreateOrderRequest request = new CreateOrderRequest();
		request.setSkuId(skuId);
		request.setCount(1);
		
		if (testWithCoupon) {
			CouponVO coupon = couponClient.clearAndInsertCoupon(1L);
			request.setCouponId(coupon.getId());
		}
		
		String json = createOrder("/order/createWithCreatingSuccess", request);
		System.out.println("json: " + json);
//		CreateOrderResponse res = jsonMapper.fromJson(json, CreateOrderResponse.class);
		
		TXLogMessage skuLog = null;
		while((skuLog = findSkuServiceTXLog(skuId))==null) {
			LangUtils.await(1);
			System.out.println("wait 1 seconds...");
		}
		assertThat(skuLog.getStatus()).isEqualTo(TXStatus.CONFIRMED);
		SkuVO sku = skuClient.get(skuId);
		assertThat(sku).isNotNull();
		assertThat(sku.getFrozenStockCount()).isEqualTo(0);
		assertThat(sku.getStockCount()).isEqualTo(createSku.getStockCount()-request.getCount());
		
		if (testWithCoupon) {
			CouponVO coupon = this.couponClient.get(request.getCouponId());
			assertThat(coupon).isNotNull();
			assertThat(coupon.getStatus()).isEqualTo(CouponStatus.USED);
		}
	}
}

