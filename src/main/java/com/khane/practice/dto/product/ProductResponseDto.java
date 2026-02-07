package com.khane.practice.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@Data
public class ProductResponseDto {

    private UUID id;
    private String name;
    private String description;
    private String category;
    private Integer quantity;
    private BigDecimal price;
    private UUID userid;
}
