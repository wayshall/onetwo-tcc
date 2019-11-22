package org.onetwo.tcc.core.internal;

import org.onetwo.ext.ons.producer.ONSProducerServiceImpl;
import org.onetwo.tcc.core.TCCProperties;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author weishao zeng
 * <br/>
 */
public class TCCProducerService extends ONSProducerServiceImpl {
	
	@Value(TCCProperties.PRODUER_ID)
	private String producerId;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.setProducerId(producerId);
		super.afterPropertiesSet();
	}

}

