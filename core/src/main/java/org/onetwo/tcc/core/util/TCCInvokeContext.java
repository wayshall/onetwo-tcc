package org.onetwo.tcc.core.util;
/**
 * @author weishao zeng
 * <br/>
 */

import org.onetwo.tcc.core.internal.TransactionResourceHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class TCCInvokeContext {
	public static final Object CONTEXT_BIND_KEY = new Object();
	
//	private static final NamedThreadLocal<TransactionResourceHolder> CONTEXTS = new NamedThreadLocal<>("tcc-invoke-context");
	
	public static void set(TransactionResourceHolder context) {
		if (context!=null) {
//			CONTEXTS.set(context);
			TransactionSynchronizationManager.bindResource(CONTEXT_BIND_KEY, context);
		}
	}
	public static void remove() {
//		CONTEXTS.remove();
		TransactionSynchronizationManager.unbindResource(CONTEXT_BIND_KEY);
	}
	
	public static TransactionResourceHolder get() {
//		return CONTEXTS.get();
		return (TransactionResourceHolder)TransactionSynchronizationManager.getResource(CONTEXT_BIND_KEY);
	}

}

