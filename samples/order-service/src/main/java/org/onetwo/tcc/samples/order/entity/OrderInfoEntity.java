
package org.onetwo.tcc.samples.order.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.SafeHtml;
import org.onetwo.dbm.annotation.DbmIdGenerator;
import org.onetwo.dbm.id.SnowflakeGenerator;
import org.onetwo.dbm.jpa.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/***
 * 订单表
 */
@SuppressWarnings("serial")
@Entity
@Table(name="order_info")
@Data
@EqualsAndHashCode(callSuper=true)
public class OrderInfoEntity extends BaseEntity  {

    @Id
    //@GeneratedValue(strategy=GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.AUTO, generator="snowflake") 
    @DbmIdGenerator(name="snowflake", generatorClass=SnowflakeGenerator.class)
    @NotNull
    Long id;
    
    /***
     * 商品数量
     */
    @NotNull
    Integer skuCount;
    
    /***
     * 价格
     */
    @NotNull
    Double price;
    
    /***
     * 商品id
     */
    @NotNull
    Long skuId;
    
    /***
     * 名称
     */
    @NotNull
    @NotBlank
    @Length(max=100)
    @SafeHtml
    String title;
    
    /***
     * 状态：
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    OrderStatus status;
    
    /***
     * 状态：
待支付：CREATED
已支付：PAID
     * @author way
     *
     */
    public static enum OrderStatus {
    	CREATED,
    	PAID,
    	CREATING,
    	CANCEL
    }
    
}