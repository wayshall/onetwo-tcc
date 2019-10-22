package org.onetwo.tcc.spi;

import java.util.Optional;

/**
 * @author weishao zeng
 * <br/>
 */
public interface GlobalTransactionIdLookupService {
	
	/****
	 * find the global transaction id for current tcc transaction 
	 * 
	 * @author weishao zeng
	 * @return
	 */
	Optional<String> findCurrentGTID();

}

