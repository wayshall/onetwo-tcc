package org.onetwo.tcc.core.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class TCCTransactionSynchronization extends TransactionSynchronizationAdapter {
	
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	final private TransactionResourceHolder resourceHolder;
	
	public TCCTransactionSynchronization(TransactionResourceHolder resourceHolder){
		this.resourceHolder = resourceHolder;
	}
	
	@Override
	public int getOrder() {
		return DataSourceUtils.CONNECTION_SYNCHRONIZATION_ORDER + 100;
	}

	/***
	 * 挂起事务
	 */
	@Override
	public void suspend() {
		TransactionSynchronizationManager.unbindResource(resourceHolder.getTransactionAspect());
	}

	/***
	 * 恢复事务
	 */
	@Override
	public void resume() {
		TransactionSynchronizationManager.bindResource(resourceHolder.getTransactionAspect(), resourceHolder);
	}

	@Override
	public void beforeCommit(boolean readOnly) {
		if(logger.isDebugEnabled()){
			logger.debug("tcc transaction synchronization committing ");
		}
		resourceHolder.updateTxLogCommitted();
	}

	@Override
	public void afterCompletion(int status) {
		if (status==TCCTransactionSynchronization.STATUS_COMMITTED) {
		} else {
			resourceHolder.updateTxLogRollbacked();
		}
	}
	
}
