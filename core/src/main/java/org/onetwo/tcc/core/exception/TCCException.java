package org.onetwo.tcc.core.exception;

import org.onetwo.common.exception.BaseException;
import org.onetwo.common.exception.ErrorType;

/**
 * @author weishao zeng
 * <br/>
 */
public class TCCException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -628405541558972553L;
	
	protected static final String DEFAULT_ERR_CODE = "ERR_TCC";

	public TCCException(String msg) {
		super(msg);
		this.code = DEFAULT_ERR_CODE;
	}

	public TCCException(ErrorType exceptionType) {
		super(exceptionType);
		this.code = DEFAULT_ERR_CODE;
	}

	public TCCException(ErrorType exceptionType, Throwable cause) {
		super(exceptionType, cause);
	}
}

