package org.onetwo.tcc.util;

import org.onetwo.common.exception.ErrorType;

import lombok.AllArgsConstructor;

/**
 * @author weishao zeng
 * <br/>
 */
@AllArgsConstructor
public enum TCCErrors implements ErrorType {
	ERR_ONLYONE_TCC_TRANSACTIONAL("Only one @TCCTransactional per transaction");
	
	private String message;

	@Override
	public String getErrorCode() {
		return name();
	}

	@Override
	public String getErrorMessage() {
		return message;
	}

}

