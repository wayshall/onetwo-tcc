package org.onetwo.tcc.core.util;
/**
 * @author weishao zeng
 * <br/>
 */

import org.onetwo.tcc.core.internal.TransactionResourceHolder;
import org.springframework.core.NamedThreadLocal;

public class TCCInvokeContext {
	
	private static final NamedThreadLocal<TransactionResourceHolder> CONTEXTS = new NamedThreadLocal<>("tcc-invoke-context");
	
	public static void set(TransactionResourceHolder context) {
		if (context==null) {
			CONTEXTS.remove();
		} else {
			CONTEXTS.set(context);
		}
	}
	public static void remove() {
		CONTEXTS.remove();
	}
	
	public static TransactionResourceHolder get() {
		return CONTEXTS.get();
	}

}

