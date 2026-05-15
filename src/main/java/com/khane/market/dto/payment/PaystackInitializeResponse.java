package com.khane.market.dto.payment;


import java.util.UUID;

import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.AllArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Data

public class PaystackInitializeResponse {

    private String message;
    private boolean success;
    private String authorizationUrl;
    private String accessCode;
    private String paystackReference;
    private UUID orderId;

public PaystackInitializeResponse(UUID orderId, String reference, String accessCode, String authUrl) {

        this.message = "Payment initialization successful";
        this.success = true;
        this.authorizationUrl = authUrl;
        this.accessCode = accessCode;
        this.paystackReference = reference;
        this.orderId = orderId;

        }

}
