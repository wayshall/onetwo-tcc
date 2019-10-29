package org.onetwo.tcc.core.internal.message;

import java.io.Serializable;

import org.onetwo.tcc.core.util.GTXActions;

import lombok.Data;

/**
 * @author weishao zeng
 * <br/>
 */
@SuppressWarnings("serial")
@Data
public class GTXLogMessage implements Serializable {
	
	private String id;
	private GTXActions action;
	
}

