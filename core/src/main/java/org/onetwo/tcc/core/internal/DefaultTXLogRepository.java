package org.onetwo.tcc.core.internal;

import java.util.List;

import org.onetwo.common.db.spi.BaseEntityManager;
import org.onetwo.dbm.exception.EntityVersionException;
import org.onetwo.tcc.boot.TCCProperties;
import org.onetwo.tcc.core.entity.TXLogEntity;
import org.onetwo.tcc.core.entity.TXLogEntity.TXContentData;
import org.onetwo.tcc.core.exception.TCCStatusChangedException;
import org.onetwo.tcc.core.spi.TXLogMessagePublisher;
import org.onetwo.tcc.core.spi.TXLogRepository;
import org.onetwo.tcc.core.util.TXStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	private TXLogMessagePublisher messagePublisher;
	@Autowired
	private BaseEntityManager baseEntityManager;
	@Value(TCCProperties.SERVICE_ID)
	private String serviceId;

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public TXLogEntity create(TransactionResourceHolder resourceHolder) {
		TXLogEntity txlog = buildTXLog(resourceHolder);
		baseEntityManager.persist(txlog);
		messagePublisher.publishTXlogCreated(txlog);
		return null;
	}
	
	protected TXLogEntity buildTXLog(TransactionResourceHolder resourceHolder) {
		TXLogEntity txlog = new TXLogEntity();
		txlog.setId(resourceHolder.getCurrentTxid());
		txlog.setGlobalId(resourceHolder.getGtxId());
		txlog.setParentId(resourceHolder.getParentTxId());
		txlog.setServiceId(serviceId);
		txlog.setStatus(TXStatus.EXECUTING);
		txlog.setCompleted(false);
		txlog.setTransactionType(resourceHolder.getTransactionType());
		
		TXContentData content = new TXContentData();
		content.setTargetClass(resourceHolder.getTargetClass().getName());
		content.setTryMethod(resourceHolder.getTryMethod().getName());
		content.setConfirmMethod(resourceHolder.getConfirmMethod());
		content.setCancelMethod(resourceHolder.getCancelMethod());
		content.setArguments(resourceHolder.getMethodArgs());
		txlog.setContent(content);
		
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
		} else {
			messagePublisher.publishTXlogCommitted(resourceHolder.getTxlog());
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
		} else {
			messagePublisher.publishTXlogRollbacked(resourceHolder.getTxlog());
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

	/****
	 * search by globalId and serviceId.
	 * filter with serviceId for one db multip service.
	 */
	@Override
	public List<TXLogEntity> findListByGTXId(String txId) {
		List<TXLogEntity> txlogs = baseEntityManager.from(TXLogEntity.class)
													.where()
														.field("globalId").is(txId)
														.field("serviceId").is(serviceId)
														.field("completed").is(false)
													.toQuery()
													.list();
		return txlogs;
	}

	@Transactional(propagation=Propagation.MANDATORY)
	@Override
	public TXLogEntity completed(TXLogEntity txlog) {
		// update to completed or delete
		txlog.setCompleted(true);
		update(txlog);
		messagePublisher.publishTXlogCompleted(txlog);
		return txlog;
	}

}

