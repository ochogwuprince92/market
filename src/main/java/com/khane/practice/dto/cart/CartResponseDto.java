package com.khane.practice.dto.cart;

import com.khane.practice.dto.product.ProductResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CartResponseDto {

//    Cart Id
    private UUID id;
//    Owner
    private UUID userId;
//    List of products in the cart
    private List<ProductResponseDto> products;

}
