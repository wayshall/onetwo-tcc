package org.onetwo.tcc.core.exception;

import org.onetwo.common.exception.ErrorType;

/**
 * 网络io，超时……
 * 
 * @author weishao zeng
 * <br/>
 */
@SuppressWarnings("serial")
public class TCCRemoteException extends TCCException {
	
	protected static final String DEFAULT_ERR_CODE = TCCErrors.ERR_REMOTE.name();

	public TCCRemoteException(String msg) {
		super(msg);
		this.code = DEFAULT_ERR_CODE;
	}

	public TCCRemoteException(ErrorType exceptionType) {
		super(exceptionType);
		this.code = DEFAULT_ERR_CODE;
	}

	public TCCRemoteException(ErrorType exceptionType, Throwable cause) {
		super(exceptionType, cause);
	}
}

