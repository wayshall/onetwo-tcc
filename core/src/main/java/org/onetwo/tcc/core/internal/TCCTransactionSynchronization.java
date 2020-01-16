package org.onetwo.tcc.core.internal;

import org.onetwo.common.spring.SpringUtils;
import org.onetwo.tcc.core.util.TCCInvokeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;

public class TCCTransactionSynchronization extends TransactionSynchronizationAdapter {
	
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	final private TransactionResourceHolder resourceHolder;
	
	public TCCTransactionSynchronization(TransactionResourceHolder resourceHolder){
		this.resourceHolder = resourceHolder;
	}
	
	@Override
	public int getOrder() {
//		return SpringUtils.higherThan(DataSourceUtils.CONNECTION_SYNCHRONIZATION_ORDER, 100);
		return SpringUtils.lowerThan(DataSourceUtils.CONNECTION_SYNCHRONIZATION_ORDER, 100);
	}

	/***
	 * 事务挂起时，不挂起tcc上下文，否则在新的事务里的远程调用里获取不到事务上下文
	 */
	@Override
	public void suspend() {
//		TCCInvokeContext.remove();
	}

	/***
	 * 没有挂起，所以不用恢复事务
	 */
	@Override
	public void resume() {
//		TCCInvokeContext.set(resourceHolder);
	}

	@Override
	public void beforeCommit(boolean readOnly) {
		if(logger.isDebugEnabled()){
			logger.debug("tcc transaction synchronization committing!");
		}
		resourceHolder.updateTxLogCommitted();
	}

	@Override
	public void afterCompletion(int status) {
		if(logger.isDebugEnabled()){
			logger.debug("tcc transaction synchronization completed!");
		}
		TCCInvokeContext.remove();
		if (status==TCCTransactionSynchronization.STATUS_ROLLED_BACK) {
			resourceHolder.updateTxLogRollbacked();
		}
	}
	
}
