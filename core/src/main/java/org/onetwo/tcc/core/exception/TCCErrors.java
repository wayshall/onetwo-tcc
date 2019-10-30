package org.onetwo.tcc.core.exception;

import org.onetwo.common.exception.ErrorType;

import lombok.AllArgsConstructor;

/**
 * @author weishao zeng
 * <br/>
 */
@AllArgsConstructor
public enum TCCErrors implements ErrorType {
	/***
	 * 既找不到调用的事务上下文，也不是标记为可作为全局事务的方法
	 */
	ERR_NOT_GLOBALIZED_METHOD("Neither globalized tcc-method, nor found parent tcc context"),
	ERR_ONLYONE_TCC_TRANSACTIONAL("Only one @TCCTransactional per transaction!"),
	ERR_TX_STATUS_CHANGED("The status of transaction has changed!"),
	ERR_WEB_REQUEST_NOT_FOUND("the request not found in context!"),
	
	ERR_TCC_METHODS_NOT_FOUND("Confirm or cancel method not found!"),
	ERR_TCC_METHODS_TOO_MANY("Too many confirm or cancel method!"),
	ERR_TOO_MANY_CONFIRM("Too many confirm methods!"),
	ERR_TOO_MANY_CANCEL("Too many cancel methods!"),
	
	ERR_REMOTE("Invoke remote error!");
	
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

