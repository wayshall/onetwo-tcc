package org.onetwo.tcc.internal;

import org.onetwo.ext.alimq.ConsumContext;
import org.onetwo.ext.ons.annotation.ONSConsumer;
import org.onetwo.ext.ons.annotation.ONSSubscribe;
import org.onetwo.ext.ons.annotation.ONSSubscribe.IdempotentType;
import org.onetwo.tcc.TCCProperties;
import org.onetwo.tcc.internal.message.TXLogMessage;
import org.onetwo.tcc.spi.LocalTransactionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * 动态消费组，每个服务一个
 * 
 * @author weishao zeng
 * <br/>
 */
@ONSConsumer
@Slf4j
@Transactional
public class TXLogConsumer {
	
	@Autowired
	private LocalTransactionHandler transactionHandler;
	
	@ONSSubscribe(topic=TCCProperties.TOPIC, 
				tags=TCCProperties.TAG_TXLOG, 
				consumerId=TCCProperties.CONSUMER_TXLOG, 
				idempotent=IdempotentType.DATABASE)
	public void consumeTXLog(ConsumContext context, TXLogMessage txlogMessage) {
		if (log.isInfoEnabled()) {
			log.info("received txlog message： {}", txlogMessage);
		}
		transactionHandler.handle(txlogMessage);
	}
	
}

