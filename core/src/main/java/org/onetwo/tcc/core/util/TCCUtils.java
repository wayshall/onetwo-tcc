package org.onetwo.tcc.core.util;
/**
 * @author weishao zeng
 * <br/>
 */

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

import org.onetwo.common.log.JFishLoggerFactory;
import org.onetwo.common.spring.SpringUtils;
import org.onetwo.common.utils.LangUtils;
import org.onetwo.common.utils.StringUtils;
import org.onetwo.tcc.core.annotation.TCCTransactional;
import org.onetwo.tcc.core.exception.TCCErrors;
import org.onetwo.tcc.core.exception.TCCException;
import org.slf4j.Logger;
import org.springframework.core.annotation.AnnotationUtils;
import org.onetwo.common.utils.Assert;

public class TCCUtils {
	
	private static final Logger logger = JFishLoggerFactory.getLogger("org.onetwo.tcc.log");

	public static Logger getLogger() {
		return logger;
	}
	
	/****
	 * 检查约束
	 * @author weishao zeng
	 */
	public static void checkTccMethods(TCCTransactionalMeta meta, boolean isGlobalTX) {
		if (StringUtils.isBlank(meta.getConfirmMethod())) {
			if (!isGlobalTX) {
				throw new TCCException("current tcc method is a branch transaction, the confirmMethod of [" + meta.getTryMethod() + "] can not be blank!");
			}
		} else {
			TCCUtils.checkAndSelectMethod(meta.getTargetClass(), meta.getConfirmMethod(), meta.getTryMethod());
		}
		if (StringUtils.isBlank(meta.getCancelMethod())) {
			if (!isGlobalTX) {
				throw new TCCException("current tcc method is a branch transaction, the cancelMethod of [" + meta.getTryMethod() + "] can not be blank!");
			}
		} else {
			TCCUtils.checkAndSelectMethod(meta.getTargetClass(), meta.getCancelMethod(), meta.getTryMethod());
		}
	}
	
	public static Optional<TCCTransactionalMeta> findTCCTransactionalMeta(Class<?> targetClass, Method tryMethod) {
		TCCTransactional tcc = AnnotationUtils.findAnnotation(tryMethod, TCCTransactional.class);
		if (tcc==null) {
			return Optional.empty();
		}
		TCCTransactionalMeta meta = new TCCTransactionalMeta();
		meta.setCancelMethod(tcc.cancelMethod());
		meta.setConfirmMethod(tcc.confirmMethod());
		meta.setTryMethod(tryMethod);
		meta.setGlobalized(tcc.globalized());
		meta.setTargetClass(targetClass!=null?targetClass:tryMethod.getDeclaringClass());
		return Optional.of(meta);
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

