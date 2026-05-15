package com.khane.market.controller;

import com.khane.market.dto.cart.CartRequestDto;
import com.khane.market.dto.cart.CartResponseDto;
import com.khane.market.entity.cart.Cart;
import com.khane.market.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    //  Create user for cart
    @PostMapping("/user/{userId}")
    public ResponseEntity <CartResponseDto> createCartForUser(
                                            @PathVariable UUID userId){
        CartResponseDto cartResponseDto = cartService.createCartForUser(userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(cartResponseDto);
    }

    //  Get cart by user
    @GetMapping("/user/{userId}")
    public ResponseEntity <CartResponseDto> getCartByUser(
                                            @PathVariable UUID userId){
        CartResponseDto cartResponseDto = cartService.getCartByUser(userId);

        return ResponseEntity.ok(cartResponseDto);
    }

    // add product to cart
    @PostMapping("/addProduct")
    public ResponseEntity<CartResponseDto> addProductToCart(
                @RequestBody @Valid CartRequestDto cartRequestDto) {
        CartResponseDto cartResponseDto = cartService.addProductToCart(cartRequestDto);
        return ResponseEntity.ok(cartResponseDto);
    }

    // remove product from cart
    @DeleteMapping("/{cartId}/product/{productId}")
    public ResponseEntity<CartResponseDto> removeProductFromCart(@PathVariable UUID cartId,
                                      @PathVariable UUID productId) {
        CartResponseDto cartResponseDto = cartService.removeProductFromCart(cartId, productId);

        return ResponseEntity.ok(cartResponseDto);
    }

}

