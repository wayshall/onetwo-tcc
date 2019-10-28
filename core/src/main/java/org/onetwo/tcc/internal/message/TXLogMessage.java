package org.onetwo.tcc.internal.message;

import org.onetwo.tcc.util.TXActions;

import lombok.Data;

/**
 * @author weishao zeng
 * <br/>
 */
@Data
public class TXLogMessage {
	
	private String id;
	private TXActions action;

}

