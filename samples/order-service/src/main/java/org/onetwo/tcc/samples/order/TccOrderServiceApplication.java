package org.onetwo.tcc.samples.order;

import org.onetwo.common.apiclient.impl.RestExecutorConfiguration;
import org.onetwo.dbm.spring.EnableDbm;
import org.onetwo.tcc.boot.EnableTCC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author weishao zeng
 * <br/>
 */
@SpringBootApplication
@RestController
@EnableDbm
@EnableCaching
@EnableAsync
@EnableTCC
@EnableFeignClients
@Import(RestExecutorConfiguration.class)
public class TccOrderServiceApplication {

	
	public static void main(String[] args) {
		SpringApplication.run(TccOrderServiceApplication.class, args);
	}
}

