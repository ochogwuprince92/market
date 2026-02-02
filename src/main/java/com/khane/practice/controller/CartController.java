package com.khane.practice.controller;

import com.khane.practice.entity.cart.Cart;
import com.khane.practice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    //  Create user for cart
    @PostMapping("/user/{userId}")
    public Cart createCartForUser(@PathVariable UUID userId){
        return cartService.createCartForUser(userId);
    }

    //  Get cart by user
    @GetMapping("/user/{userId}")
    public Cart getCartByUser(@PathVariable UUID userId){
        return cartService.getCartByUser(userId);
    }

    // add product to cart
    @PostMapping("/{cartId}/product/{productId}")
    public Cart addProductToCart(@PathVariable UUID cartId,
                                 @PathVariable UUID productId) {
        return cartService.addProductToCart(cartId, productId);
    }

    // remove product from cart
    @DeleteMapping("/{cartId}/product/{productId}")
    public Cart removeProductFromCart(@PathVariable UUID cartId,
                                      @PathVariable UUID productId) {
        return cartService.removeProductFromCart(cartId, productId);
    }

}

