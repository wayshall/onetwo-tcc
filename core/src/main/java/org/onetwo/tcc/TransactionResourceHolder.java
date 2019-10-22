package org.onetwo.tcc;

import org.onetwo.tcc.util.TCCTransactionType;
import org.springframework.transaction.support.ResourceHolderSupport;

import lombok.Data;
import lombok.EqualsAndHashCode;

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

