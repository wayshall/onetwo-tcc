package org.onetwo.tcc.samples.product;

import java.util.Set;

import org.onetwo.boot.module.swagger.AbstractSwaggerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

import springfox.documentation.RequestHandler;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig extends AbstractSwaggerConfig {
	

    @SuppressWarnings("unchecked")
	@Bean
    public Docket api(){
    	Set<Predicate<RequestHandler>> packages = Sets.newHashSet(
    												RequestHandlerSelectors.basePackage(TccProductServiceApplication.class.getPackage().getName())
    											);
    	Docket docket = createDocket("default", "tcc-product-service", packages);
    	return docket;
    }
    
}
