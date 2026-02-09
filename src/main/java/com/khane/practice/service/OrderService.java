package com.khane.practice.service;

import com.khane.practice.dto.order.OrderRequestDto;
import com.khane.practice.dto.order.OrderResponseDto;
import com.khane.practice.dto.product.ProductResponseDto;
import com.khane.practice.entity.cart.Cart;
import com.khane.practice.entity.order.Order;
import com.khane.practice.entity.order.OrderStatus;
import com.khane.practice.entity.user.User;
import com.khane.practice.exception.CartNotFoundException;
import com.khane.practice.exception.OrderNotFoundException;
import com.khane.practice.exception.UserNotFoundException;
import com.khane.practice.repository.CartRepository;
import com.khane.practice.repository.OrderRepository;
import com.khane.practice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    // Create order
    public OrderResponseDto createOrder(UUID userId, OrderRequestDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Cart cart = cartRepository.findById(dto.getCartId())
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));

        Order order = new Order();
        order.setUser(user);
        order.setCart(cart);
        order.setStatus(OrderStatus.NEW);

        Order savedOrder = orderRepository.save(order);
        return mapToOrderResponse(savedOrder);
    }

    // Get order by ID
    public OrderResponseDto getOrderById(UUID orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        return mapToOrderResponse(order);
    }

    // Get orders by user
    public List<OrderResponseDto> getOrdersByUser(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return orderRepository.findByUser(user)
                .stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    // Update order status
    public OrderResponseDto updateOrderStatus(UUID orderId, String status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        Order updatedOrder = orderRepository.save(order);

        return mapToOrderResponse(updatedOrder);
    }

    // Map Order to OrderResponseDto
    private OrderResponseDto mapToOrderResponse(Order order) {

        List<ProductResponseDto> products = order.getCart().getProducts()
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

        BigDecimal totalPrice = products.stream()
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new OrderResponseDto(
                order.getId(),
                order.getUser().getId(),
                order.getCart().getId(),
                order.getStatus(),
                products,
                totalPrice,
                order.getCreatedAt()
        );
    }
}
