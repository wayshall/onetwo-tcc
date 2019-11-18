package org.onetwo.tcc.core.internal;

import org.onetwo.ext.ons.producer.ONSProducerServiceImpl;
import org.onetwo.tcc.core.TCCProperties;

/**
 * @author weishao zeng
 * <br/>
 */
public class TCCProducerService extends ONSProducerServiceImpl {

	@Override
	public void afterPropertiesSet() throws Exception {
		this.setProducerId(TCCProperties.PRODUER_ID);
	}

}

