package org.onetwo.tcc.feign;

import org.onetwo.tcc.feign.FeignTCCConfiguration.TCCFeignContextConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import feign.Feign;

/**
 * @author weishao zeng
 * <br/>
 */
@ConditionalOnClass(Feign.class)
@EnableFeignClients(defaultConfiguration=TCCFeignContextConfiguration.class)
public class FeignTCCConfiguration {
	
	
	protected static class TCCFeignContextConfiguration {
		@Bean
		public TXHeaderRequestInterceptor txHeaderRequestInterceptor() {
			return new TXHeaderRequestInterceptor();
		}
	}

}

