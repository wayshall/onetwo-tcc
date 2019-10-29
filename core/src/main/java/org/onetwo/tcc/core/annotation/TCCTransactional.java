package org.onetwo.tcc.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author weishao zeng
 * <br/>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TCCTransactional {

	/****
	 * confirm method
	 * 
	 * @author weishao zeng
	 * @return
	 */
	String confirmMethod();
	
	String cancelMethod();
}

