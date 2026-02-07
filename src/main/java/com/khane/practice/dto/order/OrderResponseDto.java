package com.khane.practice.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OrderResponseDto {

    private UUID id;
    private UUID userId;
    private UUID cartId;
    private BigDecimal totalPrice;
    private String status;
    private LocalDateTime createdAt;
}
