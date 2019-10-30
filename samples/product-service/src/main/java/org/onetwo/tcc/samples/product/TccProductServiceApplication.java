package org.onetwo.tcc.samples.product;

import org.onetwo.dbm.spring.EnableDbm;
import org.onetwo.ext.ons.annotation.EnableONSClient;
import org.onetwo.ext.ons.annotation.ONSProducer;
import org.onetwo.tcc.boot.EnableTCC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
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
@EnableONSClient(producers={
		@ONSProducer(producerId="product-common-produer")
})
@EnableTCC
public class TccProductServiceApplication {

	
	public static void main(String[] args) {
		SpringApplication.run(TccProductServiceApplication.class, args);
	}
}

