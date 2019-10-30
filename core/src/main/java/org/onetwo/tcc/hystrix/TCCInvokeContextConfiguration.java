package org.onetwo.tcc.hystrix;

import org.onetwo.tcc.hystrix.TCCInvokeContextConfiguration.TCCInvokeContextCondition;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import com.netflix.hystrix.Hystrix;

/**
 * @author wayshall
 * <br/>
 */
@Configuration
@Conditional(TCCInvokeContextCondition.class)
public class TCCInvokeContextConfiguration {

	@Bean
	public TCCInvokeContextConcurrencyStrategy springRequestContextConcurrencyStrategy(){
		return new TCCInvokeContextConcurrencyStrategy();
	}
	
	/***
	 * 所有内嵌类匹配的时候才匹配
	 * @author wayshall
	 *
	 */
	public static class TCCInvokeContextCondition extends AllNestedConditions {

		public TCCInvokeContextCondition() {
			super(ConfigurationPhase.REGISTER_BEAN);
		}

		@ConditionalOnProperty(name = "jfish.tcc.hystrix.shareRequestContext", matchIfMissing=true)
		static class ShareRequestContext {
		}

		/***
		 * 若cloud模块已启用了，则不再启用
		 * @author way
		 *
		 */
		@ConditionalOnProperty(name = "jfish.cloud.hystrix.shareRequestContext", havingValue="false", matchIfMissing=true)
		static class JFishCloudShareRequestContextDisabled {
		}

		/***
		 * 以前默认为true，Dalston版本后修改了默认值
		 * 参看：HystrixFeignConfiguration
		 * https://github.com/spring-cloud/spring-cloud-netflix/issues/1277
		 * @author wayshall
		 *
		 */
		/*@ConditionalOnProperty(name = "feign.hystrix.enabled")
		static class feignHystrixConfig {
		}*/

		@ConditionalOnClass(Hystrix.class)
		static class OnHystrix {
		}
	}

}
