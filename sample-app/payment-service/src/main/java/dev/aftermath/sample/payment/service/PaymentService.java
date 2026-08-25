package dev.aftermath.sample.payment.service;

import dev.aftermath.sample.payment.client.CouponClient;
import dev.aftermath.sample.payment.model.CouponResponse;
import dev.aftermath.sample.payment.model.PaymentRequest;
import dev.aftermath.sample.payment.model.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final CouponClient couponClient;

    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing payment request for customer: {}, amount: {}", request.getCustomerId(), request.getAmount());
        
        double discount = 0.0;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            CouponResponse coupon = couponClient.getCoupon(request.getCouponCode());
            if (coupon != null && coupon.isValid()) {
                // INTENTIONAL FAILURE:
                // If coupon.getDiscount() is null (e.g. PREMIUM50), calling .doubleValue() throws NullPointerException!
                discount = coupon.getDiscount().doubleValue();
            }
        }

        double finalAmount = Math.max(0.0, request.getAmount() - discount);
        String transactionId = "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        log.info("Payment processed successfully: transactionId={}, finalAmount={}", transactionId, finalAmount);

        return PaymentResponse.builder()
                .transactionId(transactionId)
                .finalAmount(finalAmount)
                .status("SUCCESS")
                .build();
    }
}
