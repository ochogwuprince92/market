package com.khane.practice.service;

import com.khane.practice.dto.cart.CartRequestDto;
import com.khane.practice.dto.cart.CartResponseDto;
import com.khane.practice.dto.product.ProductResponseDto;
import com.khane.practice.entity.cart.Cart;
import com.khane.practice.entity.product.Product;
import com.khane.practice.entity.user.User;
import com.khane.practice.exception.CartNotFoundException;
import com.khane.practice.exception.UserNotFoundException;
import com.khane.practice.repository.CartRepository;
import com.khane.practice.repository.ProductRepository;
import com.khane.practice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // ------------------------------
    // Create cart for user explicitly
    // ------------------------------
    public CartResponseDto createCartForUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Check if cart already exists
        Cart existingCart = cartRepository.findByUser(user).orElse(null);
        if (existingCart != null) return mapToCartResponse(existingCart);

        Cart cart = new Cart();
        cart.setUser(user);
        Cart savedCart = cartRepository.save(cart);
        return mapToCartResponse(savedCart);
    }

    // ------------------------------
    // Add product to cart
    // ------------------------------

    public CartResponseDto addProductToCart(CartRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Automatically get cart or create if missing
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        // Add each product
        for (UUID productId : requestDto.getProductIds()) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new CartNotFoundException("Product not found: " + productId));

            int quantity = requestDto.getQuantity() == null || requestDto.getQuantity() <= 0
                    ? 1 : requestDto.getQuantity();

            for (int i = 0; i < quantity; i++) {
                cart.getProducts().add(product);
            }
        }

        Cart savedCart = cartRepository.save(cart);
        return mapToCartResponse(savedCart);
    }

    // ------------------------------
    // Get cart by user
    // ------------------------------
    public CartResponseDto getCartByUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user"));

        return mapToCartResponse(cart);
    }

    // ------------------------------
    // Remove product from cart
    // ------------------------------
    public CartResponseDto removeProductFromCart(UUID cartId, UUID productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));

        cart.getProducts().removeIf(p -> p.getId().equals(productId));
        Cart savedCart = cartRepository.save(cart);

        return mapToCartResponse(savedCart);
    }

    // ------------------------------
    // Map Cart entity to CartResponseDto
    // ------------------------------
    private CartResponseDto mapToCartResponse(Cart cart) {
        List<ProductResponseDto> productDtos = cart.getProducts()
                .stream()
                .map(p -> new ProductResponseDto(
                        p.getId(),
                        p.getName(),
                        p.getDescription(),
                        p.getCategory(),
                        p.getQuantity(),
                        p.getPrice(),
                        p.getUser() != null ? p.getUser().getId() : null
                ))
                .toList();

        return new CartResponseDto(
                cart.getId(),
                cart.getUser().getId(),
                productDtos
        );
    }
}
