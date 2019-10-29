package org.onetwo.tcc.core.spi;

import org.onetwo.tcc.core.internal.message.GTXLogMessage;

/**
 * @author weishao zeng
 * <br/>
 */
public interface LocalTransactionHandler {
	
	void handle(GTXLogMessage txlogMessage);

}

