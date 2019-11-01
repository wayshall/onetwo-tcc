package org.onetwo.tcc.core.internal.event;

import org.onetwo.tcc.core.internal.message.GTXLogMessage;
import org.springframework.context.ApplicationEvent;

/**
 * @author weishao zeng
 * <br/>
 */
@SuppressWarnings("serial")
public class GTXLogEvent extends ApplicationEvent {
	
	private GTXLogMessage body;

	public GTXLogEvent(Object source, GTXLogMessage body) {
		super(source);
		this.body = body;
	}

	public GTXLogMessage getBody() {
		return body;
	}

	@Override
	public String toString() {
		return "GTXLogEvent [body=" + body + "]";
	}

}
