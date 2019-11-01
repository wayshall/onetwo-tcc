package org.onetwo.tcc.samples.order;

import org.onetwo.common.exception.ServiceException;
import org.onetwo.tcc.core.internal.event.GTXLogEvent;
import org.onetwo.tcc.core.util.GTXActions;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author weishao zeng
 * <br/>
 */
@Slf4j
@Component
public class ErrorOnRollbackListenner implements ApplicationListener<GTXLogEvent> {
	
	public volatile boolean enabled = false;

	@Override
	public void onApplicationEvent(GTXLogEvent event) {
		if (!enabled) {
			log.info("event listener disabled...");
			return ;
		}
		
		if (event.getBody().getAction()==GTXActions.ROLLBACKED) {
			throw new ServiceException("error on rollback");
		}
		
	}

}
