package org.onetwo.tcc.boot;

import java.util.ArrayList;
import java.util.List;

import org.onetwo.common.spring.context.AbstractImportSelector;
import org.onetwo.tcc.feign.FeignTCCConfiguration;
import org.onetwo.tcc.hystrix.TCCInvokeContextConfiguration;
import org.onetwo.tcc.resttemplate.RestTemplateSupportConfiguration;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author wayshall
 * <br/>
 */
public class EnableTCCSelector extends AbstractImportSelector<EnableTCC>{

	@Override
	protected List<String> doSelect(AnnotationMetadata metadata, AnnotationAttributes attributes) {
		List<String> classNames = new ArrayList<String>();

		classNames.add(TCCConfiguration.class.getName());
		classNames.add(FeignTCCConfiguration.class.getName());
		classNames.add(TCCInvokeContextConfiguration.class.getName());
		classNames.add(RestTemplateSupportConfiguration.class.getName());
		
		return classNames;
	}

}
