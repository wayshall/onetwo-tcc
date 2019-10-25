package org.onetwo.tcc.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 正在执行：EXECUTING
已提交：COMMITED
已回滚：ROLLBACKED
已确认：CONFIRMED
已取消：CANCELED
只能回滚（主事务超时，正在执行的分支事务只能回滚）：RB_ONLY
超时（主事务才有）：TIMEOUT
 * @author weishao zeng
 * <br/>
 */
@AllArgsConstructor
public enum TXStatus {
	EXECUTING("正在执行"),
	COMMITED("已提交"),
	ROLLBACKED("已回滚"),
	RB_ONLY("只能回滚"),
	CONFIRMED("已确认"),
	CANCELED("已取消"),
	TIMEOUT("超时")
	;
	
	@Getter
	private String label;
}

