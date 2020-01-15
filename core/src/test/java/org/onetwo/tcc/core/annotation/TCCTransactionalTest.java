package org.onetwo.tcc.core.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.Test;
import org.onetwo.common.reflect.ReflectUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author weishao zeng
 * <br/>
 */

public class TCCTransactionalTest {
	
	@Test
	public void testTCCTransactional() {
		Method doNothingMethod = ReflectUtils.findMethod(TCCTransactionTestService.class, "doNothing");
		AnnotationAttributes attributes = AnnotatedElementUtils.getMergedAnnotationAttributes(doNothingMethod, TCCTransactional.class);
		assertThat(attributes).isNotNull();
		
		attributes = AnnotatedElementUtils.getMergedAnnotationAttributes(doNothingMethod, Transactional.class);
		assertThat(attributes).isNotNull();
		assertThat(attributes.getBoolean("readOnly")).isTrue();
	}

}
