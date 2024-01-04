package org.onetwo.tcc.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import org.onetwo.dbm.annotation.DbmJsonField;
import org.onetwo.dbm.annotation.DbmJsonField.JsonConvertibleTypes;
import org.onetwo.dbm.jpa.BaseEntity;
import org.onetwo.tcc.core.util.TCCTransactionType;
import org.onetwo.tcc.core.util.TXStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
	String serviceId;
	
	@Enumerated(EnumType.ORDINAL)
	TCCTransactionType transactionType;
	@Enumerated(EnumType.STRING)
	TXStatus status;
	
	@DbmJsonField(storeTyping=true, convertibleJavaType=JsonConvertibleTypes.STRING)
	TXContentData content;
	
	@Version
	Integer dataVersion;
	
	/***
	 * 是否已完成
	 */
	@Column(name="is_completed")
	boolean completed;
	
	@Override
	public String toString() {
		return "TXLogEntity [id=" + id + ", globalId=" + globalId + ", serviceId=" + serviceId + ", transactionType="
				+ transactionType + ", status=" + status + ", dataVersion=" + dataVersion + "]";
	}
	
	public String logMessage(String message) {
		StringBuilder log = new StringBuilder(200);
		log.append("TXLog[").append(getGlobalId()).append("-").append(getId()).append("] ")
			.append(message);
		return log.toString();
	}
	
}

