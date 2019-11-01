package org.onetwo.tcc.samples.order;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.onetwo.boot.test.BootMvcBaseITestable;
import org.onetwo.common.jackson.JsonMapper;
import org.onetwo.tcc.samples.order.TccOrderBaseApplicationUTests.TccOrderTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= {TccOrderServiceApplication.class, TccOrderTestConfig.class})
@ActiveProfiles({"dev", "product"})
public class TccOrderBaseApplicationUTests implements BootMvcBaseITestable {
	
	@Autowired
	protected WebApplicationContext webApplicationContext;
	private MockMvc mockMvc;
	JsonMapper jsonMapper = JsonMapper.defaultMapper();

	@BeforeClass
	public static void setupClass(){
	}
	
	@Before
	public void initMockMvc(){
		this.mockMvc = buildMockMvc(webApplicationContext);
	}

	public MockMvc mockMvc() {
		return mockMvc;
	}


	@Configuration
	@ComponentScan
	public static class TccOrderTestConfig {
		
		@Bean
		public RestTemplate restTemplate() {
			return new RestTemplate();
		}
		
	}

}
