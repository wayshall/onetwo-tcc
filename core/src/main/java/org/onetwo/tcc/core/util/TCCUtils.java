package org.onetwo.tcc.core.util;
/**
 * @author weishao zeng
 * <br/>
 */

import java.lang.reflect.Method;
import java.util.Set;

import org.onetwo.common.log.JFishLoggerFactory;
import org.onetwo.common.spring.SpringUtils;
import org.onetwo.common.utils.LangUtils;
import org.onetwo.tcc.core.exception.TCCErrors;
import org.onetwo.tcc.core.exception.TCCException;
import org.slf4j.Logger;
import org.springframework.util.Assert;

public class TCCUtils {
	
	private static final Logger logger = JFishLoggerFactory.getLogger("org.onetwo.tcc.log");

	public static Logger getLogger() {
		return logger;
	}

	/***
	 * 只检查方法的参数个数和类型即可
	 * @author weishao zeng
	 * @param targetMethod
	 * @param sourceMethod
	 */
	public static Method checkAndSelectMethod(Class<?> targetClass, String targetMethod, Method sourceMethod) {
		Assert.hasText(targetMethod, "targetMethod must have text");
		Set<Method> methods = SpringUtils.selectMethodsByParameterTypes(targetClass, targetMethod, sourceMethod);
		if (methods.size()==0) {
			throw new TCCException(TCCErrors.ERR_TCC_METHODS_NOT_FOUND);
		} else if (methods.size()>1) {
			throw new TCCException(TCCErrors.ERR_TCC_METHODS_TOO_MANY);
		}
		return LangUtils.getFirst(methods);
	}
}

