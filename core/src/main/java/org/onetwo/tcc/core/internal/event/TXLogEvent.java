package org.onetwo.tcc.core.internal.event;

import org.onetwo.tcc.core.internal.message.TXLogMessage;
import org.springframework.context.ApplicationEvent;

/**
 * @author weishao zeng
 * <br/>
 */
@SuppressWarnings("serial")
public class TXLogEvent extends ApplicationEvent {
	
	private TXLogMessage body;

	public TXLogEvent(Object source, TXLogMessage body) {
		super(source);
		this.body = body;
	}

	public TXLogMessage getBody() {
		return body;
	}

	@Override
	public String toString() {
		return "TXLogEvent [body=" + body + "]";
	}

}
