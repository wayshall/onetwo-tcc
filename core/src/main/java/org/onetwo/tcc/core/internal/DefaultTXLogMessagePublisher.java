package org.onetwo.tcc.core.internal;

import org.onetwo.boot.mq.SendMessageFlags;
import org.onetwo.common.log.JFishLoggerFactory;
import org.onetwo.common.spring.SpringUtils;
import org.onetwo.ext.alimq.OnsMessage;
import org.onetwo.ext.alimq.SimpleMessage;
import org.onetwo.ext.ons.producer.ProducerService;
import org.onetwo.tcc.boot.TCCProperties;
import org.onetwo.tcc.core.entity.TXLogEntity;
import org.onetwo.tcc.core.internal.message.GTXLogMessage;
import org.onetwo.tcc.core.internal.message.TXLogMessage;
import org.onetwo.tcc.core.spi.TXLogMessagePublisher;
import org.onetwo.tcc.core.util.GTXActions;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

/**
 * @author weishao zeng
 * <br/>
 */
public class DefaultTXLogMessagePublisher implements TXLogMessagePublisher, InitializingBean {
	private final Logger logger = JFishLoggerFactory.getLogger(TXLogMessagePublisher.class);
//	@Autowired
	private ProducerService producerService;
	@Autowired
	private ApplicationContext applicationContext;
	@Value(TCCProperties.PRODUER_ID)
	private String producerId;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.producerService = SpringUtils.getBean(applicationContext, producerId);
		Assert.notNull(producerService, "producer not found: " + producerId);
	}

	@Override
	public void publishGTXlogCommitted(TXLogEntity txlog) {
		GTXLogMessage message = new GTXLogMessage();
		message.setId(txlog.getId());
		message.setAction(GTXActions.COMMIT);

		this.publishGTXLogMessage(message, txlog);
		this.publishGTXLogEvent(message, txlog);
	}

	@Override
	public void publishGTXlogRollbacked(TXLogEntity txlog) {
		GTXLogMessage message = new GTXLogMessage();
		message.setId(txlog.getId());
		message.setAction(GTXActions.ROLLBACK);
		
		this.publishGTXLogMessage(message, txlog);
		this.publishGTXLogEvent(message, txlog);
	}
	
	protected void publishGTXLogMessage(GTXLogMessage message, TXLogEntity txlog) {
		OnsMessage onsMessage = SimpleMessage.builder()
				.topic(TCCProperties.TOPIC)
				.tags(TCCProperties.TAG_GTXLOG)
				.dataId(txlog.getId())
//				.serializer(messageSerializer)
				.body(message)
				.build();
		this.producerService.sendMessage(onsMessage, SendMessageFlags.EnableDatabaseTransactional);
	}
	
	protected void publishGTXLogEvent(GTXLogMessage message, TXLogEntity txlog) {
		this.applicationContext.publishEvent(message);
		if (logger.isInfoEnabled()) {
			logger.info(txlog.logMessage(" sent spring event: {}"), message);
		}
	}
	
	private TXLogMessage createTXLogMessage(TXLogEntity log) {
		TXLogMessage message = new TXLogMessage();
		BeanUtils.copyProperties(log, message);
		return message;
	}
	
	protected void publishTXLogEvent(TXLogMessage message, TXLogEntity log) {
		this.applicationContext.publishEvent(message);
		if (logger.isInfoEnabled()) {
			logger.info(log.logMessage(" sent spring event: {}"), message);
		}
	}
	
	protected void publishTXLogMessage(TXLogMessage message, TXLogEntity txlog) {
		OnsMessage onsMessage = SimpleMessage.builder()
				.topic(TCCProperties.TOPIC)
				.tags(TCCProperties.TAG_TXLOG)
				.dataId(txlog.getId())
//				.serializer(messageSerializer)
				.body(message)
				.build();
		this.producerService.sendMessage(onsMessage, SendMessageFlags.EnableDatabaseTransactional);
	}

	@Override
	public void publishTXlogCreated(TXLogEntity log) {
		TXLogMessage message = createTXLogMessage(log);
		publishTXLogMessage(message, log);
		publishTXLogEvent(message, log);
	}

	@Override
	public void publishTXlogCompleted(TXLogEntity log) {
		TXLogMessage message = createTXLogMessage(log);
		publishTXLogMessage(message, log);
		publishTXLogEvent(message, log);
	}

	@Override
	public void publishTXlogCommitted(TXLogEntity log) {
		TXLogMessage message = createTXLogMessage(log);
		publishTXLogMessage(message, log);
		publishTXLogEvent(message, log);
	}

	@Override
	public void publishTXlogRollbacked(TXLogEntity log) {
		TXLogMessage message = createTXLogMessage(log);
		publishTXLogMessage(message, log);
		publishTXLogEvent(message, log);
	}

}

