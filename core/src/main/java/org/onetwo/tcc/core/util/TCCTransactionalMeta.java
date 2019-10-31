package org.onetwo.tcc.core.util;

import java.lang.reflect.Method;

import lombok.Data;

/**
 * @author weishao zeng
 * <br/>
 */
@Data
public class TCCTransactionalMeta {
	
	private Class<?> targetClass;
	private boolean globalized;
	private Method tryMethod;
	private String confirmMethod;
	private String cancelMethod;

}

