package org.onetwo.tcc.core.internal;

import org.onetwo.ext.alimq.ConsumContext;
import org.onetwo.ext.ons.annotation.ONSConsumer;
import org.onetwo.ext.ons.annotation.ONSSubscribe;
import org.onetwo.ext.ons.annotation.ONSSubscribe.IdempotentType;
import org.onetwo.tcc.boot.TCCProperties;
import org.onetwo.tcc.core.internal.message.GTXLogMessage;
import org.onetwo.tcc.core.spi.LocalTransactionHandler;
import org.springframework.transaction.annotation.Transactional;

/**
 * 动态消费组，每个服务一个
 * 
 * @author weishao zeng
 * <br/>
 */
@Transactional
@ONSConsumer
public class GTXLogConsumer {
	
	private LocalTransactionHandler transactionHandler;
	
	public GTXLogConsumer(LocalTransactionHandler transactionHandler) {
		super();
		this.transactionHandler = transactionHandler;
	}

	@ONSSubscribe(topic=TCCProperties.TOPIC, 
				tags=TCCProperties.TAG_GTXLOG, 
				consumerId=TCCProperties.CONSUMER_GTXLOG, 
				idempotent=IdempotentType.DATABASE)
	public void consumeGTXLog(ConsumContext context, GTXLogMessage txlogMessage) {
		transactionHandler.handle(txlogMessage);
	}
	
}

