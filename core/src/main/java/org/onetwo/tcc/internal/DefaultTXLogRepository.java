package org.onetwo.tcc.internal;

import java.util.List;

import org.onetwo.common.db.spi.BaseEntityManager;
import org.onetwo.dbm.exception.EntityVersionException;
import org.onetwo.tcc.TransactionResourceHolder;
import org.onetwo.tcc.entity.TXLogEntity;
import org.onetwo.tcc.exception.TCCStatusChangedException;
import org.onetwo.tcc.spi.TCCMessagePublisher;
import org.onetwo.tcc.spi.TXLogRepository;
import org.onetwo.tcc.util.TXStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;

/**
 * @author weishao zeng
 * <br/>
 */
public class DefaultTXLogRepository implements TXLogRepository {

	@Autowired
	@Getter
	private TCCMessagePublisher messagePublisher;
	@Autowired
	private BaseEntityManager baseEntityManager;

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public TXLogEntity create(TransactionResourceHolder resourceHolder) {
		TXLogEntity txlog = buildTXLog(resourceHolder);
		baseEntityManager.persist(txlog);
		messagePublisher.publishTXlogCreated(txlog);
		return null;
	}
	
	protected TXLogEntity buildTXLog(TransactionResourceHolder resourceHolder) {
		TXLogEntity txlog = null;
		return txlog;
	}

	@Transactional(propagation=Propagation.MANDATORY)
	@Override
	public TXLogEntity updateToCommitted(TransactionResourceHolder resourceHolder) {
		TXLogEntity txlog = resourceHolder.getTxlog();
		txlog.setStatus(TXStatus.COMMITED);
		update(txlog);
		if (resourceHolder.isGlobalTX()) {
			messagePublisher.publishGTXlogCommitted(resourceHolder.getTxlog());
		}
		return txlog;
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public TXLogEntity updateToRollbacked(TransactionResourceHolder resourceHolder) {
		TXLogEntity txlog = resourceHolder.getTxlog();
		txlog.setStatus(TXStatus.ROLLBACKED);
		update(txlog);
		if (resourceHolder.isGlobalTX()) {
			messagePublisher.publishGTXlogRollbacked(txlog);
		}
		return txlog;
	}
	
	protected void update(TXLogEntity txlog) {
		try {
			baseEntityManager.update(txlog);
		} catch (EntityVersionException e) {
			// 一般发生在事务提交的时候，发现事务状态已被修改（如标记为RB_ONLY）
			throw new TCCStatusChangedException(e)
								.put("txlog", txlog);
		}
	}

	@Override
	public List<TXLogEntity> findListByGTXId(String txId) {
		List<TXLogEntity> txlogs = baseEntityManager.from(TXLogEntity.class)
													.where()
														.field("globalId").is(txId)
														.field("completed").is(false)
													.toQuery()
													.list();
		return txlogs;
	}

	@Transactional(propagation=Propagation.MANDATORY)
	@Override
	public TXLogEntity updateToCompleted(TXLogEntity txlog) {
		txlog.setCompleted(true); 
		update(txlog);
		messagePublisher.publishTXlogCompleted(txlog);
		return txlog;
	}

}

