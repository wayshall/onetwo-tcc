package org.onetwo.tcc.core;

import java.util.ArrayList;
import java.util.List;

import org.onetwo.common.spring.context.AbstractImportSelector;
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
		
		return classNames;
	}

}
