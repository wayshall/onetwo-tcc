package org.onetwo.tcc.core;

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
	public static final String ENABLED_KEY = PREFIX_KEY + ".enabled";

	/***
	 * for one db multip service
	 */
	public static final String SERVICE_ID = "${" + PREFIX_KEY + ".service-id:${spring.application.name}}";
	public static final String PRODUER_ID = "${" + PREFIX_KEY + ".producer-id:tcc-producer-${env.topic-prefix:tcc-producer-${spring.application.name}}}";
	/***
	 * jfish.tcc.rmq.topic
	 */
	public static final String TOPIC = "${" + PREFIX_KEY + ".rmq.topic:TCC-${env.topic-prefix:${spring.application.name}}}";
	/***
	 * 事务
	 */
	public static final String CONSUMER_GTXLOG = "${" + PREFIX_KEY + ".rmq.consumers.gtxlog:GTXLOG-${env.topic-prefix:${spring.application.name}}}";
	public static final String TAG_GTXLOG = "${" + PREFIX_KEY + ".rmq.tags.gtxlog:GTXLOG}";
	public static final String TAG_TXLOG = "${" + PREFIX_KEY + ".rmq.tags.txlog:TXLOG}";
//	public static final String CONSUMER_TXLOG = "${" + PREFIX_KEY + ".rmq.consumers:txlog-consumer}";

	private CompensationProps compensation = new CompensationProps();
	/***
	 * io error, timeout ...
	 */
	private List<String> remoteExceptions = Lists.newArrayList("org.springframework.web.client.ResourceAccessException");
	/***
	 * 是否发布事务日志消息
	 */
	private boolean publishTxlog;
	
	
	@Data
	public static class CompensationProps {
		/***
		 * in milliseconds
		 */
		public static final String PREFIX = PREFIX_KEY + ".compensation";
		public static final String TIMER_CONFIG_KEY = PREFIX + ".fixedDelayString";
		public static final String LOCKER_KEY = PREFIX + ".locker";
		

		public static final String LOCKER_REDIS = "redis";
		public static final String LOCKER_DBM = "dbm";
		
		private String lockKey = "tcc:compensation";
		private String locker = LOCKER_REDIS;
//		private String redisLockTimeout = "2m";
		/***
		 * 补充服务判断事务超时的时间
		 */
		private String timeout = "2m";
		public long getTimeoutInSeconds() {
			return LangOps.timeToSeconds(timeout, 120L);
		}
		
		public boolean isUseReidsLock() {
			return LOCKER_REDIS.equalsIgnoreCase(locker);
		}
	}
}

