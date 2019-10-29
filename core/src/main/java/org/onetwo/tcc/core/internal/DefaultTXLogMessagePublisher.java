package org.onetwo.tcc.core.internal;

import org.onetwo.boot.mq.SendMessageFlags;
import org.onetwo.common.log.JFishLoggerFactory;
import org.onetwo.ext.alimq.OnsMessage;
import org.onetwo.ext.alimq.SimpleMessage;
import org.onetwo.ext.ons.producer.ProducerService;
import org.onetwo.tcc.core.TCCProperties;
import org.onetwo.tcc.core.entity.TXLogEntity;
import org.onetwo.tcc.core.internal.message.GTXLogMessage;
import org.onetwo.tcc.core.spi.TXLogMessagePublisher;
import org.onetwo.tcc.core.util.GTXActions;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author weishao zeng
 * <br/>
 */
public class DefaultTXLogMessagePublisher implements TXLogMessagePublisher {
	private final Logger logger = JFishLoggerFactory.getLogger(TXLogMessagePublisher.class);
	@Autowired
	private ProducerService producerService;

	@Override
	public void publishGTXlogCommitted(TXLogEntity log) {
		GTXLogMessage message = new GTXLogMessage();
		message.setId(log.getId());
		message.setAction(GTXActions.COMMIT);
		
		OnsMessage onsMessage = SimpleMessage.builder()
				.topic(TCCProperties.TOPIC)
				.tags(TCCProperties.TAG_GTXLOG)
				.dataId(log.getId())
//				.serializer(messageSerializer)
				.body(message)
				.build();
		this.producerService.sendMessage(onsMessage, SendMessageFlags.EnableDatabaseTransactional);
	}

	@Override
	public void publishGTXlogRollbacked(TXLogEntity log) {
		GTXLogMessage message = new GTXLogMessage();
		message.setId(log.getId());
		message.setAction(GTXActions.ROLLBACK);
		
		OnsMessage onsMessage = SimpleMessage.builder()
				.topic(TCCProperties.TOPIC)
				.tags(TCCProperties.TAG_GTXLOG)
				.dataId(log.getId())
//				.serializer(messageSerializer)
				.body(message)
				.build();
		this.producerService.sendMessage(onsMessage, SendMessageFlags.EnableDatabaseTransactional);
	}

	@Override
	public void publishTXlogCreated(TXLogEntity log) {
		logger.info("ignore send txlog created message: {}", log);
	}

	@Override
	public void publishTXlogCompleted(TXLogEntity log) {
		logger.info("ignore send txlog completed message: {}", log);
	}

	@Override
	public void publishTXlogCommitted(TXLogEntity log) {
		logger.info("ignore send txlog committed message: {}", log);
	}

	@Override
	public void publishTXlogRollbacked(TXLogEntity log) {
		logger.info("ignore send txlog rollbacked message: {}", log);
	}

}

