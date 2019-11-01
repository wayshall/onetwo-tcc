package org.onetwo.tcc.core.timer;

import org.onetwo.boot.module.redis.RedisConfiguration;
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
	@ConditionalOnProperty(value=CompensationProps.ENABLED_REDIS_LOCK_KEY, matchIfMissing=true)
	@Import(RedisConfiguration.class)
	protected class RedisLockConfiguration {
	}
}
