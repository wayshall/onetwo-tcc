package org.onetwo.tcc.core.internal;

import org.onetwo.boot.mq.SendMessageFlags;
import org.onetwo.common.log.JFishLoggerFactory;
import org.onetwo.common.spring.SpringUtils;
import org.onetwo.ext.alimq.OnsMessage;
import org.onetwo.ext.alimq.SimpleMessage;
import org.onetwo.ext.ons.ONSProperties.MessageSerializerType;
import org.onetwo.tcc.core.TCCProperties;
import org.onetwo.tcc.core.entity.TXLogEntity;
import org.onetwo.tcc.core.internal.event.GTXLogEvent;
import org.onetwo.tcc.core.internal.event.TXLogEvent;
import org.onetwo.tcc.core.internal.message.GTXLogMessage;
import org.onetwo.tcc.core.internal.message.TXLogMessage;
import org.onetwo.tcc.core.spi.TXLogMessagePublisher;
import org.onetwo.tcc.core.util.GTXActions;
import org.onetwo.tcc.core.util.TXActions;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.onetwo.common.utils.Assert;

/**
 * @author weishao zeng
 * <br/>
 */
public class DefaultTXLogMessagePublisher implements TXLogMessagePublisher, InitializingBean {
	private final Logger logger = JFishLoggerFactory.getLogger(TXLogMessagePublisher.class);
//	@Autowired
	private TCCProducerService producerService;
	@Autowired
	private ApplicationContext applicationContext;
	@Value(TCCProperties.PRODUER_ID)
	private String producerId;
	@Autowired
	private TCCProperties tccProperties;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.producerService = SpringUtils.getBean(applicationContext, TCCProducerService.class);
		Assert.notNull(producerService, "producer not found: " + producerId);
	}

	@Override
	public void publishGTXlogCommitted(TXLogEntity txlog) {
		GTXLogMessage message = new GTXLogMessage();
		message.setId(txlog.getId());
		message.setAction(GTXActions.COMMITTED);

		this.publishGTXLogMessage(message, txlog);
		this.publishGTXLogEvent(message, txlog);
	}

	@Override
	public void publishGTXlogRollbacked(TXLogEntity txlog) {
		GTXLogMessage message = new GTXLogMessage();
		message.setId(txlog.getId());
		message.setAction(GTXActions.ROLLBACKED);
		
		this.publishGTXLogMessage(message, txlog);
		this.publishGTXLogEvent(message, txlog);
	}
	
	protected void publishGTXLogMessage(GTXLogMessage message, TXLogEntity txlog) {
		OnsMessage onsMessage = SimpleMessage.builder()
				.topic(TCCProperties.TOPIC)
				.tags(TCCProperties.TAG_GTXLOG)
				.dataId(txlog.getId())
				.serializer(MessageSerializerType.TYPING_JSON.name())
				.body(message)
				.build();
		this.producerService.sendMessage(onsMessage, SendMessageFlags.EnableDatabaseTransactional);
		if (logger.isInfoEnabled()) {
			logger.info(txlog.logMessage(" publish {} message"), message.getAction());
		}
	}
	
	protected void publishGTXLogEvent(GTXLogMessage message, TXLogEntity txlog) {
		GTXLogEvent gtxEvent = new GTXLogEvent(this, message);
		this.applicationContext.publishEvent(gtxEvent);
		if (logger.isInfoEnabled()) {
			logger.info(txlog.logMessage(" sent spring gtxlog event: {}"), gtxEvent);
		}
	}
	
	private TXLogMessage createTXLogMessage(TXLogEntity log) {
		TXLogMessage message = new TXLogMessage();
		BeanUtils.copyProperties(log, message);
		return message;
	}
	
	protected void publishTXLogEvent(TXLogMessage message, TXLogEntity log) {
		TXLogEvent event = new TXLogEvent(this, message);
		this.applicationContext.publishEvent(event);
		if (logger.isInfoEnabled()) {
			logger.info(log.logMessage(" sent spring txlog event: {}"), event);
		}
	}
	
	protected void publishTXLogMessage(TXLogMessage message, TXLogEntity txlog) {
		if (!tccProperties.isPublishTxlog()) {
			if (logger.isInfoEnabled()) {
				logger.info("config[{}.publish-TXLog] is disabled, ignore send txlog message", TCCProperties.PREFIX_KEY);
			}
			return ;
		}
		OnsMessage onsMessage = SimpleMessage.builder()
				.topic(TCCProperties.TOPIC)
				.tags(TCCProperties.TAG_TXLOG)
				.dataId(txlog.getId())
				.serializer(MessageSerializerType.TYPING_JSON.name())
				.body(message)
				.build();
		this.producerService.sendMessage(onsMessage, SendMessageFlags.EnableDatabaseTransactional);
	}

	@Override
	public void publishTXlogCreated(TXLogEntity log) {
		TXLogMessage message = createTXLogMessage(log);
		message.setActions(TXActions.CREATED);
		publishTXLogMessage(message, log);
		publishTXLogEvent(message, log);
	}

	@Override
	public void publishTXlogCompleted(TXLogEntity log) {
		TXLogMessage message = createTXLogMessage(log);
		message.setActions(TXActions.COMPLETED);
		publishTXLogMessage(message, log);
		publishTXLogEvent(message, log);
	}

	@Override
	public void publishTXlogCommitted(TXLogEntity log) {
		TXLogMessage message = createTXLogMessage(log);
		message.setActions(TXActions.COMMITTED);
		publishTXLogMessage(message, log);
		publishTXLogEvent(message, log);
	}

	@Override
	public void publishTXlogRollbacked(TXLogEntity log) {
		TXLogMessage message = createTXLogMessage(log);
		message.setActions(TXActions.ROLLBACKED);
		publishTXLogMessage(message, log);
		publishTXLogEvent(message, log);
	}

}

