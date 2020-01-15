package org.onetwo.tcc.core.annotation;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.transaction.annotation.Transactional;

/**
 * spring transactional use method, see SpringTransactionAnnotationParser
 * 
 * @author weishao zeng
 * <br/>
 */

public class TCCTransactionTestService {
	
	@TCCTransactional
	@Transactional(readOnly = true)
	public void doNothing() {
	}

}
