package org.onetwo.tcc.boot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Import;

/**
 * @author wayshall
 * <br/>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({EnableTCCSelector.class})
@Conditional(TCCEnabledKeyCondition.class)
public @interface EnableTCC {
	
	String[] basePackages() default {};
	Class<?>[] basePackageClasses() default {};

}
