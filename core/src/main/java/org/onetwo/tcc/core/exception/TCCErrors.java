package org.onetwo.tcc.core.exception;

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
	
	ERR_TCC_METHODS_NOT_FOUND("confirm or cancel method not found!"),
	ERR_TCC_METHODS_TOO_MANY("too many confirm or cancel method!"),
	ERR_TOO_MANY_CONFIRM("too many confirm methods!"),
	ERR_TOO_MANY_CANCEL("too many cancel methods!"),
	
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

