package org.onetwo.tcc.exception;

import org.onetwo.common.exception.ErrorType;

/**
 * 一般发生在事务提交的时候，发现事务状态已被修改（如标记为RB_ONLY）
 * @author weishao zeng
 * <br/>
 */
@SuppressWarnings("serial")
public class TCCStatusChangedException extends TCCException {
	
	protected static final String DEFAULT_ERR_CODE = TCCErrors.ERR_TX_STATUS_CHANGED.name();

	public TCCStatusChangedException(String msg) {
		super(msg);
		this.code = DEFAULT_ERR_CODE;
	}

	public TCCStatusChangedException(ErrorType exceptionType) {
		super(exceptionType);
		this.code = DEFAULT_ERR_CODE;
	}

	public TCCStatusChangedException(ErrorType exceptionType, Throwable cause) {
		super(exceptionType, cause);
	}

	public TCCStatusChangedException(Throwable cause) {
		super(TCCErrors.ERR_TX_STATUS_CHANGED, cause);
	}
}

