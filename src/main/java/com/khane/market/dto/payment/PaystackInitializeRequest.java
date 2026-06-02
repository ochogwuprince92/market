package com.khane.market.dto.payment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PaystackInitializeRequest {

    @NotNull(message = "Order ID is required")
    private UUID orderId;

    private BigDecimal amount;

    @NotNull(message = "Email is required")
    private String email;

    private String callbackUrl;

    private String metadata;
}

