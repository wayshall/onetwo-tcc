package org.onetwo.tcc.samples.usr.api;

import javax.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author weishao zeng <br/>
 */
@Api(tags = "Coupon Api")
@RequestMapping("/usr/coupon")
public interface CouponApi {
	
	@PostMapping(path="clearAndInsertCoupon")
	CouponVO clearAndInsertCoupon(@RequestParam("userId") Long userId);

	@PostMapping(path="frozonCoupon")
	void frozonCoupon(@RequestParam("couponId") Long couponId);

	@GetMapping(path="get")
	CouponVO get(@RequestParam("couponId") Long couponId);

	@Data
	public class CouponVO {
		Long id;

		@NotNull
		Long userId;

		/***
		 * 优惠券金额
		 */
		@NotNull
		Double price;
		
	    CouponStatus status;
	}

    /***
     * 状态：
可用：VALID
冻结：FROZON
已使用：USED
     * @author way
     *
     */
    @AllArgsConstructor
    public static enum CouponStatus {
    	VALID("可用"),
    	FROZON("冻结"),
    	USED("已使用");
    	
    	@Getter
    	private String labe;
    	
    }
}
