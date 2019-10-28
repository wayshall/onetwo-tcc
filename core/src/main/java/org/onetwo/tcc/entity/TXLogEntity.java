package org.onetwo.tcc.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.onetwo.dbm.annotation.DbmJsonField;
import org.onetwo.dbm.jpa.BaseEntity;
import org.onetwo.tcc.util.TCCTransactionType;
import org.onetwo.tcc.util.TXStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author weishao zeng
 * <br/>
 */
@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "tcc_tx_log")
public class TXLogEntity extends BaseEntity {
	
	@Id
	String id;

	String globalId;
	String parentId;
	
	@Enumerated(EnumType.ORDINAL)
	TCCTransactionType transactionType;
	@Enumerated(EnumType.STRING)
	TXStatus status;
	
	@DbmJsonField(storeTyping=true)
	TXContentData content;
	
	@Version
	Integer dataVersion;
	
	@Data
	public static class TXContentData implements Serializable {
		String targetClass;
		String tryMethod;
		String confirmMethod;
		String consumeMethod;
		Object[] arguments;
	}

	@Override
	public String toString() {
		return "TXLogEntity [id=" + id + ", globalId=" + globalId + ", parentId=" + parentId + ", transactionType="
				+ transactionType + ", status=" + status + ", dataVersion=" + dataVersion + "]";
	}
	
}

