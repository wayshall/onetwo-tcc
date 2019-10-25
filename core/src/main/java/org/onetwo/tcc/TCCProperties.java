package org.onetwo.tcc;

import org.onetwo.boot.core.config.BootJFishConfig;
import org.onetwo.common.utils.LangOps;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author weishao zeng
 * <br/>
 */
@ConfigurationProperties(prefix=TCCProperties.PREFIX_KEY)
@Data
public class TCCProperties {
	public static final String PREFIX_KEY = BootJFishConfig.PREFIX + "jfish.tcc";

	/***
	 * 事务超时时间
	 */
	private String timeout = "2m";
	private CompensationProps compensation;
	
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

