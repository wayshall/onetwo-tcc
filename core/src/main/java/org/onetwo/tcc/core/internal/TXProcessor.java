package org.onetwo.tcc.core.internal;

/**
 * @author weishao zeng
 * <br/>
 */
public interface TXProcessor {
	
	void apply(TransactionResourceHolder resource);

}

