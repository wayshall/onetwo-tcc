package org.onetwo.tcc.core.timer;

import org.onetwo.boot.module.redis.RedisConfiguration;
import org.onetwo.dbm.lock.DbmLockerConfiguration;
import org.onetwo.tcc.core.TCCProperties;
import org.onetwo.tcc.core.TCCProperties.CompensationProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author weishao zeng
 * <br/>
 */
@Configuration
public class TCCTimerConfiguration {

	@Autowired
	private TCCProperties tccProperties;
	
	@Bean
	public CompensationService compensationService() {
		CompensationService compensationService = new CompensationService();
		compensationService.setCompensationProps(tccProperties.getCompensation());
		return compensationService;
	}

	@Configuration
	@ConditionalOnMissingBean(RedisConfiguration.class)
	@ConditionalOnProperty(value=CompensationProps.LOCKER_KEY, havingValue=CompensationProps.LOCKER_REDIS, matchIfMissing=true)
	@Import(RedisConfiguration.class)
	protected class RedisLockConfiguration {
	}

	@ConditionalOnProperty(value=CompensationProps.LOCKER_KEY, havingValue=CompensationProps.LOCKER_DBM)
	@Import(DbmLockerConfiguration.class)
	protected class DBLockerConfiguration {
	}
}
