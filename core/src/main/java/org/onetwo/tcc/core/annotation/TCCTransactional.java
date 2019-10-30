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
	
	/***
	 * 用于标识是否可作为全局事务方法（即主事务开始调用的方法）
	 * 若方法是不可作为全局事务方法的，又找不到远程调用的事务上下文，则会抛错。
	 * 
	 * 当作为全局事务方法时，confirmMethod和cancelMethod可为空
	 * @author weishao zeng
	 * @return
	 */
	boolean globalized() default true;

	/****
	 * confirm method
	 * 
	 * @author weishao zeng
	 * @return
	 */
	String confirmMethod() default "";
	
	String cancelMethod() default "";
}

