package org.onetwo.tcc.boot;

import org.onetwo.boot.core.condition.EnabledKeyCondition;
import org.onetwo.tcc.core.TCCProperties;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;

public class TCCEnabledKeyCondition extends EnabledKeyCondition {

	@Override
	protected String getEnabledKey(Environment environment, AnnotationAttributes attrubutes) {
		return TCCProperties.PREFIX_KEY + ".enabled";
	}
	
}
