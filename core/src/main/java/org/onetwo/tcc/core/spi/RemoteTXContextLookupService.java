package org.onetwo.tcc.core.spi;

import java.io.Serializable;
import java.util.Optional;

import lombok.Data;

/**
 * 传递和设置事务上下文
 * 
 * @author weishao zeng
 * <br/>
 */
public interface RemoteTXContextLookupService {
	
	/****
	 * find the global transaction id for current tcc transaction 
	 * 
	 * @author weishao zeng
	 * @return
	 */
	Optional<TXContext> findCurrent();
	
//	void setCurrent(TXContext context);
	
	@SuppressWarnings("serial")
	@Data
	public class TXContext implements Serializable {
		String gtxId;
		String parentTxId;
	}

}

