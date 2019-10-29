package org.onetwo.tcc.spi;

import org.onetwo.tcc.entity.TXLogEntity;

/**
 * @author weishao zeng
 * <br/>
 */
public interface TCCMessagePublisher {
	/***
	 * 本地事务已创建
	 * @author weishao zeng
	 * @param log
	 */
	void publishTXlogCreated(TXLogEntity log);
	/***
	 * 发布一个全局事务已提交的事务消息
	 * @author weishao zeng
	 * @param log
	 */
	void publishGTXlogCommitted(TXLogEntity log);
	
	/****
	 * 发布一个全局事务已回滚的事务消息
	 * @author weishao zeng
	 * @param log
	 */
	void publishGTXlogRollbacked(TXLogEntity log);
	
	/***
	 * 本地事务已完成
	 * @author weishao zeng
	 * @param log
	 */
	void publishTXlogCompleted(TXLogEntity log);

}

