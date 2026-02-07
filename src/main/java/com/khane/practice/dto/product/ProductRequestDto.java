package com.khane.practice.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductRequestDto {

    @NotNull(message = "name cannot be empty")
    private String name;
    private String description;
    private String category;
    private Integer quantity;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 50")
    private BigDecimal price;
}
