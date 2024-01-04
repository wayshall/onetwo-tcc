
package org.onetwo.tcc.samples.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import org.onetwo.dbm.annotation.DbmIdGenerator;
import org.onetwo.dbm.id.SnowflakeGenerator;
import org.onetwo.dbm.jpa.BaseEntity;
import org.onetwo.tcc.samples.usr.api.CouponApi.CouponStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

/***
 * 用户优惠券表
 */
@SuppressWarnings("serial")
@Entity
@Table(name="usr_coupon")
@Data
@EqualsAndHashCode(callSuper=true)
public class CouponEntity extends BaseEntity  {

    @Id
    //@GeneratedValue(strategy=GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.AUTO, generator="snowflake") 
    @DbmIdGenerator(name="snowflake", generatorClass=SnowflakeGenerator.class)
    @NotNull
    Long id;
    
    /***
     * 用户名
     */
    @NotNull
    Long userId;
    
    /***
     * 优惠券金额
     */
    @NotNull
    Double price;
    
    /***
     * 订单id
     */
    Long orderId;
    
    /***
     * 状态：
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    CouponStatus status;
    
    
}