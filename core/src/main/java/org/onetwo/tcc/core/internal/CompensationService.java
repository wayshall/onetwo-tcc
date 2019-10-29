package org.onetwo.tcc.core.internal;

import java.time.LocalDateTime;

import org.onetwo.boot.module.redis.RedisLockRunner;
import org.onetwo.common.date.Dates;
import org.onetwo.common.db.spi.BaseEntityManager;
import org.onetwo.tcc.boot.TCCProperties.CompensationProps;
import org.onetwo.tcc.core.entity.TXLogEntity;
import org.onetwo.tcc.core.util.TXStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author weishao zeng
 * <br/>
 */
@Data
@Slf4j
public class CompensationService {
	
	@Autowired(required=false)
	private RedisLockRegistry redisLockRegistry;
	private CompensationProps compensationProps;
	private long timeoutInSeconds;
	@Autowired
	private BaseEntityManager baseEntityManager;
	
	@Scheduled(fixedRateString="${"+CompensationProps.FIXED_RATE_KEY+":30000}", initialDelay=30000)
	public void scheduleCheckExecutingTXLogs(){
		log.info("start to check executing TXLogs...");
		if(compensationProps.isUseReidsLock()){
			getRedisLockRunner().tryLock(()->{
				markExecutingTXLogsToTimeout();
				return null;
			});
		}else{
			markExecutingTXLogsToTimeout();
		}
		log.info("finish check executing TXLogs...");
	}

	protected void markExecutingTXLogsToTimeout() {
		LocalDateTime timeoutAt = LocalDateTime.now().minusSeconds(timeoutInSeconds);
		baseEntityManager.from(TXLogEntity.class)
						.where()
							.field("status").is(TXStatus.EXECUTING)
							.field("createAt").lessThan(Dates.toDate(timeoutAt))
						.toQuery();
	}
	

	private RedisLockRunner getRedisLockRunner(){
		RedisLockRunner redisLockRunner = RedisLockRunner.createLocker(redisLockRegistry, compensationProps.getLockKey(), compensationProps.getRedisLockTimeout());
		return redisLockRunner;
	}

}

