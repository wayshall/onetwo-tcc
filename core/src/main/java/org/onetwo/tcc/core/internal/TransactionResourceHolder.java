package org.onetwo.tcc.core.internal;

import java.lang.reflect.Method;

import org.onetwo.tcc.core.entity.TXLogEntity;
import org.onetwo.tcc.core.spi.RemoteTXContextLookupService.TXContext;
import org.onetwo.tcc.core.util.TCCTransactionType;
import org.onetwo.tcc.core.util.TCCUtils;
import org.springframework.transaction.support.ResourceHolderSupport;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

/**
 * @author weishao zeng
 * <br/>
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class TransactionResourceHolder extends ResourceHolderSupport {
	
	private TransactionAspect transactionAspect;
	private TXContext parentContext;
//	private String gid;
//	private String parentId;
	private String currentTxid;
	private String serviceId;
	
	private Class<?> targetClass;
	private Method tryMethod;
	private String confirmMethod;
	private String cancelMethod;
	private Object target;
	private Object[] methodArgs;
	private TCCTransactionType transactionType;
	@Setter(value=AccessLevel.NONE)
	private TXLogEntity txlog;
	
	
//	private List<BranchTransactionalData> branchs = new ArrayList<>();

	public TransactionResourceHolder(TransactionAspect transactionAspect) {
		super();
		this.transactionAspect = transactionAspect;
	}
	
	public String getGtxId() {
		if (parentContext!=null) {
			return parentContext.getGtxId();
		}
		return currentTxid;
	}
	
	public String getParentTxId() {
		if (parentContext!=null) {
			return parentContext.getParentTxId();
		}
		return null;
	}
	
	/****
	 * 检查约束
	 * @author weishao zeng
	 */
	public void check() {
		// check consumer method exists
		TCCUtils.checkAndSelectMethod(targetClass, confirmMethod, tryMethod);
		TCCUtils.checkAndSelectMethod(targetClass, cancelMethod, tryMethod);
		
	}
	
	
	public TXLogEntity createTxLog() {
		txlog = transactionAspect.getTxLogRepository().create(this);
		return txlog;
	}
	
	public TXLogEntity updateTxLogCommitted() {
		txlog = transactionAspect.getTxLogRepository().updateToCommitted(this);
		return txlog;
	}
	
	public TXLogEntity updateTxLogRollbacked() {
		txlog = transactionAspect.getTxLogRepository().updateToRollbacked(this);
		return txlog;
	}
	
	/***
	 * 是否全局事务
	 * @author weishao zeng
	 * @return
	 */
	public boolean isGlobalTX() {
		return this.transactionType==TCCTransactionType.GLOBAL;
	}
	
	/*public BranchTransactionalData addBranch(String confirmMethod, String cancelMethod) {
		BranchTransactionalData branch = BranchTransactionalData.builder()
														.confirmMethod(confirmMethod)
														.cancelMethod(cancelMethod)
														.txId("B" + nextId())
														.build();
		return branch;
	}*/
	

	/*@Data
	@AllArgsConstructor
	@Builder
	public static class BranchTransactionalData {
		private String txId;
		private String confirmMethod;
		private String cancelMethod;
	}
	*/
}

