package com.khane.practice.service;

import com.khane.practice.entity.cart.Cart;
import com.khane.practice.entity.product.Product;
import com.khane.practice.entity.user.User;
import com.khane.practice.repository.CartRepository;
import com.khane.practice.repository.ProductRepository;
import com.khane.practice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    public Cart createCartForUser(UUID userId) {
//      Create cart for use
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found"));

//      If user found, create cart
        Cart cart = new Cart();
//      Map to user
        cart.setUser(user);
//      Save
        return cartRepository.save(cart);
    }

//  Get cart by user
    public Cart getCartByUser(UUID userId) {

        return cartRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("Cart not found"));
    }

//  Add products to cart
    public Cart addProductToCart(UUID cartId, UUID productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new RuntimeException("Product not found"));

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(()-> new RuntimeException("Cart not found"));

        cart.getProducts().add(product);
        return cartRepository.save(cart);
    }

    public Cart removeProductFromCart(UUID cartId, UUID productId) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(()-> new RuntimeException("Cart noot found"));

        cart.getProducts()
                .removeIf(p -> Objects.equals(p.getId(), productId));


        return cartRepository.save(cart);
    }
}
