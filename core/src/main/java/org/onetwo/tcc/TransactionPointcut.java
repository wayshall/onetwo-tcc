package org.onetwo.tcc;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author weishao zeng
 * <br/>
 */
@Aspect
public class TransactionPointcut {

	@Pointcut("@annotation(org.onetwo.tcc.annotation.TCCTransactional)")
	public void tccTransactional(){}

}

