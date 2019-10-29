package org.onetwo.tcc.core.spi;

import java.util.List;

import org.onetwo.tcc.core.entity.TXLogEntity;
import org.onetwo.tcc.core.internal.TransactionResourceHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author weishao zeng
 * <br/>
 */
public interface TXLogRepository {
	
	/***
	 * 创建事务日志，独立事务
	 * @author weishao zeng
	 * @param resourceHolder
	 */
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	TXLogEntity create(TransactionResourceHolder resourceHolder);
	
	/***
	 * 更新事务日志为已提交
	 * 更新操作的事务必须在本地的业务事务中，以保持事务状态和业务操作的一致
	 * @author weishao zeng
	 * @param resourceHolder
	 * @return
	 */
	@Transactional(propagation=Propagation.MANDATORY)
	TXLogEntity updateToCommitted(TransactionResourceHolder resourceHolder);
	

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	TXLogEntity updateToRollbacked(TransactionResourceHolder resourceHolder);

	/****
	 * 根据事务id查找所有相关事务
	 * @author weishao zeng
	 * @param txId
	 * @return
	 */
	List<TXLogEntity> findListByGTXId(String txId);
	

	@Transactional(propagation=Propagation.MANDATORY)
	TXLogEntity completed(TXLogEntity txlog);
}

