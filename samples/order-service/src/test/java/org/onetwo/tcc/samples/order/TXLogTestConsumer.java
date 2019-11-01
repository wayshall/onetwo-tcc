package org.onetwo.tcc.samples.order;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.MultiMapUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.onetwo.ext.alimq.ConsumContext;
import org.onetwo.ext.ons.annotation.ONSConsumer;
import org.onetwo.ext.ons.annotation.ONSSubscribe;
import org.onetwo.tcc.core.TCCProperties;
import org.onetwo.tcc.core.internal.message.TXLogMessage;
import org.springframework.transaction.annotation.Transactional;

/**
 * 动态消费组，每个服务一个
 * 
 * @author weishao zeng
 * <br/>
 */
@Transactional
@ONSConsumer
public class TXLogTestConsumer {

//	public Map<Long, CountDownLatch> latchMap = Maps.newConcurrentMap();
	public static MultiValuedMap<String, TXLogMessage> txlogMessageMap = MultiMapUtils.newListValuedHashMap();
	volatile private static List<TXLogMessage> txLogMessageList = new ArrayList<>();
	
	@ONSSubscribe(topic=TCCProperties.TOPIC, 
				tags=TCCProperties.TAG_TXLOG, 
				consumerId="test-order-txlog-assert")
	public void consumeTXLog(ConsumContext context, TXLogMessage txlogMessage) {
		txlogMessageMap.put(txlogMessage.getGlobalId(), txlogMessage);
		txLogMessageList.add(txlogMessage);
	}
	
	static public List<TXLogMessage> getTXLogMessageList() {
		return new ArrayList<>(txLogMessageList);
	}
	
}

