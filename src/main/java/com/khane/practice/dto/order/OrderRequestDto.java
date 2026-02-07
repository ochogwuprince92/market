package com.khane.practice.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class OrderRequestDto {

    @NotNull(message = "user ID is required")
    private UUID userId;

    @NotNull(message = "cart ID is required")
    private UUID cartId;
}
