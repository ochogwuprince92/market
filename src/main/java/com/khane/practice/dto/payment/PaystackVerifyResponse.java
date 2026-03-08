package com.khane.practice.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaystackVerifyResponse {

    private boolean status;
    private String message;
    private PaymentData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentData {
        private Long id;
        private String reference;
        private Long amount;
        private String status;
        private String customer;
        private LocalDateTime paidAt;
        private String authorization;
        private PaymentAuthorization authorizationData;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentAuthorization {
        private String authorizationUrl;
        private String accessCode;
        private String reference;
        private String channel;
        private String cardType;
        private String bank;
        private String countryCode;
    }
}

