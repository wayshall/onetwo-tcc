package org.onetwo.tcc.resttemplate;

import java.util.List;

import org.onetwo.common.apiclient.RestExecutor;
import org.onetwo.common.spring.SpringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Lists;

/**
 * @author weishao zeng
 * <br/>
 */
@Configuration
@ConditionalOnClass(RestTemplate.class)
public class RestTemplateSupportConfiguration {
	
	@Bean
	public TXClientHttpRequestInterceptor txClientHttpRequestInterceptor() {
		return new TXClientHttpRequestInterceptor();
	}
	
	@Bean
	public SpringRestTemplateConfigurer springRestTemplateConfigurer() {
		return new SpringRestTemplateConfigurer();
	}
	
	public class SpringRestTemplateConfigurer implements InitializingBean {

//		private List<RestTemplate> restTemplates;
		@Autowired
		private ApplicationContext applicationContext;
		@Autowired
		private TXClientHttpRequestInterceptor txClientHttpRequestInterceptor;
		
		@Override
		public void afterPropertiesSet() throws Exception {
			List<RestTemplate> restTemplates = SpringUtils.getBeans(applicationContext, RestTemplate.class);
			SpringUtils.getBeans(applicationContext, RestExecutor.class).forEach(re -> {
				if (re instanceof RestTemplate) {
					restTemplates.add((RestTemplate)re);
				}
			});
			restTemplates.forEach(restTemplate -> {
				configRestTemplate(restTemplate);
			});
		}

		protected void configRestTemplate(RestTemplate restTemplate) {
//			TXClientHttpRequestInterceptor
			List<ClientHttpRequestInterceptor> interList = restTemplate.getInterceptors();
			if(interList==null){
				interList = Lists.newArrayList();
				restTemplate.setInterceptors(interList);
			}
			interList.add(txClientHttpRequestInterceptor);
			AnnotationAwareOrderComparator.sort(interList);
		}

	}

}

