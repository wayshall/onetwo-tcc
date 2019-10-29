package org.onetwo.tcc.core;
/**
 * @author weishao zeng
 * <br/>
 */

import org.onetwo.common.interceptor.SimpleInterceptorManager;
import org.onetwo.tcc.core.internal.DefaultLocalTransactionHandler;
import org.onetwo.tcc.core.internal.DefaultRemoteTXContextLookupService;
import org.onetwo.tcc.core.internal.DefaultTXLogMessagePublisher;
import org.onetwo.tcc.core.internal.DefaultTXLogRepository;
import org.onetwo.tcc.core.internal.GTXLogConsumer;
import org.onetwo.tcc.core.internal.TransactionAspect;
import org.onetwo.tcc.core.spi.LocalTransactionHandler;
import org.onetwo.tcc.core.spi.TCCTXContextLookupService;
import org.onetwo.tcc.core.spi.TXInterceptor;
import org.onetwo.tcc.core.spi.TXLogMessagePublisher;
import org.onetwo.tcc.core.spi.TXLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TCCProperties.class)
public class TCCConfiguration {
	
	@Autowired
	private TCCProperties tccProperties;
	
	@Bean
	public TCCTXContextLookupService globalTransactionIdLookupService() {
		return new DefaultRemoteTXContextLookupService();
	}
	
	@Bean
	public TransactionAspect transactionAspect(TCCTXContextLookupService globalTransactionIdLookupService,
			TXLogRepository txLogRepository) {
		TransactionAspect transactionAspect = new TransactionAspect(tccInterceptorManager(), globalTransactionIdLookupService, txLogRepository);
		transactionAspect.setRemoteExceptions(tccProperties.getRemoteExceptions());
		return transactionAspect;
	}
	
	@Bean
	public SimpleInterceptorManager<TXInterceptor> tccInterceptorManager() {
		SimpleInterceptorManager<TXInterceptor> manager = new SimpleInterceptorManager<>(TXInterceptor.class);
		return manager;
	}
	
	
	
	
	
	@Bean
	public GTXLogConsumer gtLogConsumer(LocalTransactionHandler localTransactionHandler) {
		return new GTXLogConsumer(localTransactionHandler);
	}
	
	@Bean
	public LocalTransactionHandler localTransactionHandler() {
		return new DefaultLocalTransactionHandler();
	}
	
	
	
	
	
	@Bean
	public TXLogRepository txlogRepository() {
		return new DefaultTXLogRepository();
	}
	
	@Bean
	public TXLogMessagePublisher tccMessagePublisher() {
		return new DefaultTXLogMessagePublisher();
	}

}

