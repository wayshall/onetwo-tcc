
package org.onetwo.tcc.samples.user.service.impl;

import org.onetwo.common.db.spi.BaseEntityManager;
import org.onetwo.tcc.core.annotation.TCCService;
import org.onetwo.tcc.core.annotation.TCCTransactional;
import org.onetwo.tcc.samples.user.entity.CouponEntity;
import org.onetwo.tcc.samples.usr.api.CouponApi.CouponStatus;
import org.onetwo.tcc.samples.usr.api.CouponApi.CouponVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@TCCService
public class CouponServiceImpl {

    @Autowired
    private BaseEntityManager baseEntityManager;
    
    public CouponVO clearAndInsertCoupon(Long userId) {
    	baseEntityManager.removeAll(CouponEntity.class);
    	CouponEntity coupon = new CouponEntity();
    	coupon.setUserId(userId);
    	coupon.setPrice(1.0);
    	coupon.setStatus(CouponStatus.VALID);
    	
    	baseEntityManager.save(coupon);
    	
    	return coupon.asBean(CouponVO.class);
    }
    
    public CouponVO get(Long couponId) {
    	CouponEntity coupon = baseEntityManager.load(CouponEntity.class, couponId);
    	return coupon.asBean(CouponVO.class);
    }
    
    @TCCTransactional(globalized=false, confirmMethod="commitCoupon", cancelMethod="cancelCoupon")
    public void frozonCoupon(Long couponId) {
    	CouponEntity coupon = baseEntityManager.load(CouponEntity.class, couponId);
    	coupon.setStatus(CouponStatus.FROZON);
    	baseEntityManager.update(coupon);
    }
    
    public void commitCoupon(Long couponId) {
    	CouponEntity coupon = baseEntityManager.load(CouponEntity.class, couponId);
    	coupon.setStatus(CouponStatus.USED);
    	baseEntityManager.update(coupon);
    }
    
    public void cancelCoupon(Long couponId) {
    	CouponEntity coupon = baseEntityManager.load(CouponEntity.class, couponId);
    	coupon.setStatus(CouponStatus.VALID);
    	baseEntityManager.update(coupon);
    }
    
}