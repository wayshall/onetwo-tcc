package org.onetwo.tcc.samples.order;
/**
 * @author weishao zeng
 * <br/>
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.onetwo.common.utils.LangUtils;
import org.onetwo.tcc.core.internal.message.TXLogMessage;
import org.onetwo.tcc.core.util.TXStatus;
import org.onetwo.tcc.samples.api.SkuApi.SkuVO;
import org.onetwo.tcc.samples.order.client.SkuClient;
import org.onetwo.tcc.samples.order.vo.CreateOrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Commit;

@Commit
public class TccOrderServiceITest extends TccOrderBaseApplicationUTests {

    @Autowired
    private SkuClient skuClient;
    
	@Test
	public void testCreateOrder() throws Exception {
		Long skuId = 1L;
		SkuVO createSku = new SkuVO();
		createSku.setStockCount(100);
		createSku.setId(skuId);
		createSku.setName("韭菜");
		skuClient.cleanAndCreateSku(createSku);
		
		CreateOrderRequest request = new CreateOrderRequest();
		request.setSkuId(skuId);
		request.setCount(1);
		String json = createOrder(request);
		System.out.println("json: " + json);
//		CreateOrderResponse res = jsonMapper.fromJson(json, CreateOrderResponse.class);
		
		TXLogMessage skuLog = null;
		while((skuLog = findSkuServiceTXLog())==null) {
			LangUtils.await(1);
			System.out.println("wait 1 seconds...");
		}
		assertThat(skuLog.getStatus()).isEqualTo(TXStatus.CONFIRMED);
		SkuVO sku = skuClient.get(createSku.getId());
		assertThat(sku).isNotNull();
		assertThat(sku.getFrozenStockCount()).isEqualTo(0);
		assertThat(sku.getStockCount()).isEqualTo(createSku.getStockCount()-request.getCount());
	}
	
	private TXLogMessage findSkuServiceTXLog() {
		TXLogMessage txlog = TXLogTestConsumer.txLogMessageList.stream().filter(log -> {
			return log.getServiceId().equals("tcc-product-service") && 
					log.isCompleted();
		}).findFirst().orElse(null);
		return txlog;
	}
	
	private String createOrder(CreateOrderRequest request) throws Exception {
		String json = perform(post("/order").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
											.accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
											.content(jsonMapper.toJson(request))
							)
							.andExpect(status().isOk())
							.andExpect(jsonPath("$.id").isNotEmpty() )
							.andReturn().getResponse().getContentAsString();
		return json;
	}

}

