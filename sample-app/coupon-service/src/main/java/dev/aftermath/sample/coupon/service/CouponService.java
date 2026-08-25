package dev.aftermath.sample.coupon.service;

import dev.aftermath.sample.coupon.model.CouponResponse;
import org.springframework.stereotype.Service;

@Service
public class CouponService {

    public CouponResponse getCoupon(String code) {
        if ("SAVE10".equalsIgnoreCase(code)) {
            return new CouponResponse(code, 10.0, true);
        } else if ("SAVE20".equalsIgnoreCase(code)) {
            return new CouponResponse(code, 20.0, true);
        } else if ("PREMIUM50".equalsIgnoreCase(code)) {
            // Intentional bug: Returns valid=true but discount=null simulating corrupt/missing data
            return new CouponResponse(code, null, true);
        }
        return new CouponResponse(code, 0.0, false);
    }
}
