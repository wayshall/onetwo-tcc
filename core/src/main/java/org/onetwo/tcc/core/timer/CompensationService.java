package org.onetwo.tcc.core.timer;

import java.time.LocalDateTime;
import java.util.List;

import org.onetwo.boot.module.redis.RedisLockRunner;
import org.onetwo.common.date.Dates;
import org.onetwo.common.db.spi.BaseEntityManager;
import org.onetwo.tcc.boot.TCCProperties.CompensationProps;
import org.onetwo.tcc.core.entity.TXLogEntity;
import org.onetwo.tcc.core.spi.TXLogRepository;
import org.onetwo.tcc.core.util.TCCTransactionType;
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
	@Autowired
	private BaseEntityManager baseEntityManager;
	@Autowired
	private TXLogRepository txLogRepository;
	
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
		LocalDateTime timeoutAt = LocalDateTime.now().minusSeconds(compensationProps.getTimeoutInSeconds());
		List<TXLogEntity> gtxlogList = baseEntityManager.from(TXLogEntity.class)
						.where()
							.field("status").is(TXStatus.EXECUTING)
							.field("transactionType").is(TCCTransactionType.GLOBAL)
							.field("createAt").lessThan(Dates.toDate(timeoutAt))
						.toQuery()
						.list();
		for(TXLogEntity txlog : gtxlogList) {
			markGTXTimeout(txlog);
		}
	}
	
	protected void markGTXTimeout(TXLogEntity txlog) {
		try {
			txLogRepository.updateGTXToTimeout(txlog);
			if (log.isInfoEnabled()) {
				log.info(txlog.logMessage(" global transaction has been timeouted"));
			}
		} catch (Exception e) {
			log.error(txlog.logMessage(" global transaction mark timeout error: " + e.getMessage()), e);
		}
	}
	

	private RedisLockRunner getRedisLockRunner(){
		RedisLockRunner redisLockRunner = RedisLockRunner.createLocker(redisLockRegistry, compensationProps.getLockKey(), compensationProps.getRedisLockTimeout());
		return redisLockRunner;
	}

}

