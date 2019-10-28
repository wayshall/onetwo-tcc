package org.onetwo.tcc.spi;

import org.onetwo.tcc.internal.message.TXLogMessage;

/**
 * @author weishao zeng
 * <br/>
 */
public interface LocalTransactionHandler {
	
	void handle(TXLogMessage txlogMessage);

}

