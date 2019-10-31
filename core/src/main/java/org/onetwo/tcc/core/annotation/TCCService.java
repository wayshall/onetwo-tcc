package org.onetwo.tcc.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记为tcc service
 * 一般情况下不需要，当标记为tcc service时，会在启动时检查tcc方法参数是否匹配
 * @author wayshall
 * <br/>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TCCService {

}
