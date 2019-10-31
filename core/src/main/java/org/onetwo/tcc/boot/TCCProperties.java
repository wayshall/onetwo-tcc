package org.onetwo.tcc.boot;

import java.util.List;

import org.onetwo.boot.core.config.BootJFishConfig;
import org.onetwo.common.utils.LangOps;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.google.common.collect.Lists;

import lombok.Data;

/**
 * @author weishao zeng
 * <br/>
 */
@ConfigurationProperties(prefix=TCCProperties.PREFIX_KEY)
@Data
public class TCCProperties {
	
	/***
	 * jfish.tcc
	 */
	public static final String PREFIX_KEY = BootJFishConfig.PREFIX + ".tcc";

	/***
	 * for one db multip service
	 */
	public static final String SERVICE_ID = "${" + PREFIX_KEY + ".service-id:${spring.application.name}}";
	public static final String PRODUER_ID = "${" + PREFIX_KEY + ".producer-id:producer-${spring.application.name}}";
	/***
	 * jfish.tcc.topic.name
	 */
	public static final String TOPIC = "${" + PREFIX_KEY + ".rmq.topic.name:TCC}";
	/***
	 * 事务
	 */
	public static final String CONSUMER_GTXLOG = "${" + PREFIX_KEY + ".rmq.consumer.gtxlog:GTXLOG-${spring.application.name}}";
	public static final String TAG_GTXLOG = "${" + PREFIX_KEY + ".rmq.tags.gtxlog:GTXLOG}";
	public static final String TAG_TXLOG = "${" + PREFIX_KEY + ".rmq.tags.txlog:TXLOG}";
//	public static final String CONSUMER_TXLOG = "${" + PREFIX_KEY + ".rmq.consumers:txlog-consumer}";

	/***
	 * 事务超时时间
	 */
	private String timeout = "2m";
	private CompensationProps compensation;
	private List<String> remoteExceptions = Lists.newArrayList("org.springframework.web.client.ResourceAccessException");
	
	public long getTimeoutInSeconds() {
		return LangOps.timeToSeconds(timeout, 120L);
	}
	
	@Data
	public static class CompensationProps {
		/***
		 * in milliseconds
		 */
		public static final String FIXED_RATE_KEY = PREFIX_KEY+".fixedRateString";
		
		private String lockKey = "locker:onetwo-tcc:compensation";
		private boolean useReidsLock;
		private String redisLockTimeout;
	}
}

