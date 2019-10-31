package org.onetwo.tcc.core.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * @author weishao zeng
 * <br/>
 */
@SuppressWarnings("serial")
@Data
public class TXContentData implements Serializable {
	String targetClass;
	String tryMethod;
	String confirmMethod;
	String cancelMethod;
	Object[] arguments;
}

