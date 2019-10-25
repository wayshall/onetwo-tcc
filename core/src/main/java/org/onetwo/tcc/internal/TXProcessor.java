package org.onetwo.tcc.internal;

import org.onetwo.tcc.TransactionResourceHolder;

/**
 * @author weishao zeng
 * <br/>
 */
public interface TXProcessor {
	
	void apply(TransactionResourceHolder resource);

}

