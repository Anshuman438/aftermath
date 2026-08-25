package dev.aftermath.sample.coupon.controller;

import dev.aftermath.sample.coupon.model.CouponResponse;
import dev.aftermath.sample.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/{code}")
    public ResponseEntity<CouponResponse> getCoupon(@PathVariable String code) {
        CouponResponse response = couponService.getCoupon(code);
        return ResponseEntity.ok(response);
    }
}
