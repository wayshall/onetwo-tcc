package org.onetwo.tcc.exception;

import org.onetwo.common.exception.ErrorType;

import lombok.AllArgsConstructor;

/**
 * @author weishao zeng
 * <br/>
 */
@AllArgsConstructor
public enum TCCErrors implements ErrorType {
	ERR_ONLYONE_TCC_TRANSACTIONAL("Only one @TCCTransactional per transaction!"),
	ERR_TX_STATUS_CHANGED("the status of transaction has changed!"),
	ERR_REMOTE("invoke remote error!");
	
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

