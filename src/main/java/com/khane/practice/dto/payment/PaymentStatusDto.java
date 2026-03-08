package com.khane.practice.dto.payment;

import java.util.UUID;
import java.time.LocalDateTime;

import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentStatusDto {

    private String message;
    private String paymentMethod;
    private LocalDateTime paidAt;
    private Long amountPaid;
    private String paystackReference;
    private String status;
    private UUID orderId;


}



