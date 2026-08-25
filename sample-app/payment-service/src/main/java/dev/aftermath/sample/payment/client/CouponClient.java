package dev.aftermath.sample.payment.client;

import dev.aftermath.sample.payment.model.CouponResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class CouponClient {

    private final WebClient webClient;

    public CouponClient(@Value("${coupon-service.base-url:http://localhost:8081}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public CouponResponse getCoupon(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        return this.webClient.get()
                .uri("/api/coupons/{code}", code)
                .retrieve()
                .bodyToMono(CouponResponse.class)
                .block();
    }
}
