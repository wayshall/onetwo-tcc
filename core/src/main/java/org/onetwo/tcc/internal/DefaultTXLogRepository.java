package org.onetwo.tcc.internal;

import org.onetwo.tcc.TransactionResourceHolder;
import org.onetwo.tcc.entity.TXLogEntity;
import org.onetwo.tcc.spi.TCCMessagePublisher;
import org.onetwo.tcc.spi.TXLogRepository;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.Getter;

/**
 * @author weishao zeng
 * <br/>
 */
public class DefaultTXLogRepository implements TXLogRepository {

	@Autowired
	@Getter
	private TCCMessagePublisher messagePublisher;
	
	@Override
	public TXLogEntity create(TransactionResourceHolder resourceHolder) {
		messagePublisher.publishTXlogCreated(log);
		return null;
	}

	@Override
	public TXLogEntity updateToCommitted(TransactionResourceHolder resourceHolder) {
		if (resourceHolder.isGlobalTX()) {
			messagePublisher.publishGTXlogCommitted(log);
		}
		return null;
	}

	@Override
	public TXLogEntity updateToRollbacked(TransactionResourceHolder resourceHolder) {
		if (resourceHolder.isGlobalTX()) {
			messagePublisher.publishGTXlogRollbacked(log);
		}
		return null;
	}
	
	

}

