package com.khane.market.dto.cart;

import com.khane.market.dto.product.ProductResponseDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CartRequestDto {

//    Owner
    @NotNull(message = "user ID is required")
    private UUID userId;
    private List<UUID> productIds;
    private Integer quantity;

}
