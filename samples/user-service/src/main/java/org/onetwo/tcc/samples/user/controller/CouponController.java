

package org.onetwo.tcc.samples.user.controller;

import org.onetwo.tcc.samples.user.service.impl.CouponServiceImpl;
import org.onetwo.tcc.samples.usr.api.CouponApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CouponController implements CouponApi {

    @Autowired
    private CouponServiceImpl couponService;
    

	public CouponVO clearAndInsertCoupon(@RequestParam("userId") Long userId) {
    	return couponService.clearAndInsertCoupon(userId);
    }
    
    public void frozonCoupon(@RequestParam("couponId") Long couponId) {
    	this.couponService.frozonCoupon(couponId);
    }
    
    public CouponVO get(@RequestParam("couponId") Long couponId) {
    	return this.couponService.get(couponId);
    }
    
}