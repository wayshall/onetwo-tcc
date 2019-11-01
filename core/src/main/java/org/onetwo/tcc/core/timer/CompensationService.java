package org.onetwo.tcc.core.timer;

import java.time.LocalDateTime;
import java.util.List;

import org.onetwo.boot.module.redis.RedisLockRunner;
import org.onetwo.common.date.Dates;
import org.onetwo.common.db.spi.BaseEntityManager;
import org.onetwo.common.spring.SpringUtils;
import org.onetwo.tcc.core.TCCProperties;
import org.onetwo.tcc.core.TCCProperties.CompensationProps;
import org.onetwo.tcc.core.entity.TXLogEntity;
import org.onetwo.tcc.core.spi.TXLogRepository;
import org.onetwo.tcc.core.util.TCCTransactionType;
import org.onetwo.tcc.core.util.TXStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author weishao zeng
 * <br/>
 */
@Data
@Slf4j
public class CompensationService implements InitializingBean {
	
//	@Autowired(required=false)
	private RedisLockRegistry redisLockRegistry;
	private CompensationProps compensationProps;
	@Autowired
	private BaseEntityManager baseEntityManager;
	@Autowired
	private TXLogRepository txLogRepository;
	@Autowired
	private ApplicationContext applicationContext;
	@Value(TCCProperties.SERVICE_ID)
	private String serviceId;


	@Override
	public void afterPropertiesSet() throws Exception {
		if (compensationProps.isUseReidsLock()) {
			RedisLockRegistry redisLock = SpringUtils.getBean(applicationContext, RedisLockRegistry.class);
			if (redisLock==null) {
				throw new IllegalStateException("config[" + CompensationProps.ENABLED_REDIS_LOCK_KEY + "] is enabled, "
						+ "but RedisLockRegistry not found!");
			}
			this.redisLockRegistry = redisLock;
		}
	}

	
	
	@Scheduled(fixedRateString="${"+CompensationProps.FIXED_RATE_KEY+":30000}", initialDelay=30000)
	@Transactional
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
							.field("serviceId").is(serviceId)
							.field("transactionType").is(TCCTransactionType.GLOBAL)
							.field("createAt").lessEqual(Dates.toDate(timeoutAt))
						.toQuery()
						.list();
		log.info("find [{}] executing txlog...", gtxlogList.size());
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
		RedisLockRunner redisLockRunner = RedisLockRunner.createLocker(redisLockRegistry, compensationProps.getLockKey(), null);
		return redisLockRunner;
	}
	
}

