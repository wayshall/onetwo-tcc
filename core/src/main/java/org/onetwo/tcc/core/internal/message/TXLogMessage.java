package org.onetwo.tcc.core.internal.message;

import java.io.Serializable;
import java.util.Date;

import org.onetwo.tcc.core.util.TCCTransactionType;
import org.onetwo.tcc.core.util.TXActions;
import org.onetwo.tcc.core.util.TXStatus;

import lombok.Data;
import lombok.ToString;

/**
 * @author weishao zeng
 * <br/>
 */
@SuppressWarnings("serial")
@Data
@ToString
public class TXLogMessage implements Serializable {
	String id;

	String globalId;
	String parentId;
	String serviceId;
	
	TCCTransactionType transactionType;
	TXStatus status;
	
	boolean completed;
	
	Date occurAt = new Date();
	
	TXActions actions;
	

	public boolean isGlobalTX() {
		return this.transactionType==TCCTransactionType.GLOBAL;
	}
}

