package org.onetwo.tcc;

import org.onetwo.tcc.entity.TXLogEntity;
import org.onetwo.tcc.util.TCCTransactionType;
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
	private String gtid;
	private String currentTid;
	
	private Class<?> targetClass;
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
	
	/****
	 * 检查约束
	 * @author weishao zeng
	 */
	public void check() {
		// check consumer method exists
	}
	
	public TXLogEntity createTxLog() {
		txlog = transactionAspect.getTxLogRepository().create(this);
		return txlog;
	}
	
	public TXLogEntity updateTxLogCommitted() {
		TXLogEntity log = transactionAspect.getTxLogRepository().updateToCommitted(this);
		return log;
	}
	
	public TXLogEntity updateTxLogRollbacked() {
		TXLogEntity log = transactionAspect.getTxLogRepository().updateToRollbacked(this);
		return log;
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

