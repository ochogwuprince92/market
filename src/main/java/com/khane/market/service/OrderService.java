package com.khane.market.service;

import com.khane.market.dto.order.OrderRequestDto;
import com.khane.market.dto.order.OrderResponseDto;
import com.khane.market.dto.payment.PaystackInitializeRequest;
import com.khane.market.dto.payment.PaystackInitializeResponse;
import com.khane.market.dto.payment.PaymentStatusDto;
import com.khane.market.dto.product.ProductResponseDto;
import com.khane.market.entity.cart.Cart;
import com.khane.market.entity.order.Order;
import com.khane.market.entity.order.OrderStatus;
import com.khane.market.entity.user.User;
import com.khane.market.exception.CartNotFoundException;
import com.khane.market.exception.OrderNotFoundException;
import com.khane.market.exception.UserNotFoundException;
import com.khane.market.repository.CartRepository;
import com.khane.market.repository.OrderRepository;
import com.khane.market.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PaystackService paystackService;

    // Create order
    @Transactional
    public OrderResponseDto createOrder(UUID userId, OrderRequestDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Cart cart = cartRepository.findById(dto.getCartId())
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));

        if (cart.getProducts() == null || cart.getProducts().isEmpty()) {
            throw new IllegalStateException("Cannot create order from an empty cart");
        }

        BigDecimal totalPrice = cart.getProducts()
                .stream()
                .map(p -> p.getPrice() != null ? p.getPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPrice.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalStateException("Cannot create order with zero total");
        }

        log.info("Creating order for user: {} with total: {}", userId, totalPrice);

        Order order = new Order();
        order.setUser(user);
        order.setCart(cart);
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.NEW);
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        log.info("Order created: {} with totalPrice: {}", savedOrder.getId(), savedOrder.getTotalPrice());

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
    public OrderResponseDto updateOrderStatus(UUID orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        order.setStatus(status);
        return mapToOrderResponse(orderRepository.save(order));
    }

    // Initiate payment
    @Transactional
    public PaystackInitializeResponse initiatePayment(UUID orderId, String userEmail, String callbackUrl) {
        log.info("Initiating payment for order: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        if (order.getTotalPrice() == null) {
            throw new IllegalStateException("Order total price is null for order: " + orderId);
        }

        if (order.getPaystackReference() != null) {
            log.warn("Payment already initiated for order: {}", orderId);
            throw new IllegalStateException("Payment has already been initiated for this order");
        }

        log.info("Order totalPrice for payment: {}", order.getTotalPrice());

        PaystackInitializeRequest request = new PaystackInitializeRequest();
        request.setOrderId(orderId);
        request.setAmount(order.getTotalPrice());
        request.setEmail(userEmail);
        request.setCallbackUrl(callbackUrl);

        PaystackInitializeResponse response = paystackService.initializePayment(request);

        order.setPaystackReference(response.getPaystackReference());
        order.setPaystackAccessCode(response.getAccessCode());
        order.setAuthorizationUrl(response.getAuthorizationUrl());
        order.setStatus(OrderStatus.PENDING);

        orderRepository.save(order);

        log.info("Payment initiated successfully for order: {}", orderId);
        return response;
    }

    // Complete payment
    @Transactional
    public PaymentStatusDto completePayment(String paystackReference) {
        log.info("Completing payment with reference: {}", paystackReference);

        Order order = orderRepository.findByPaystackReference(paystackReference)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found for reference: " + paystackReference));

        var verifyResponse = paystackService.verifyPayment(paystackReference);

        if (!"success".equalsIgnoreCase(verifyResponse.getData().getStatus())) {
            log.warn("Payment verification failed for reference: {}", paystackReference);
            return createPaymentStatusDto(order, "failed", "Payment was not successful");
        }

        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        order.setPaymentMethod("Paystack");
        orderRepository.save(order);

        // Clear cart after successful payment
        Cart cart = order.getCart();
        if (cart != null) {
            cart.getProducts().clear();
            cartRepository.save(cart);
            log.info("Cart cleared after payment for order: {}", order.getId());
        }

        log.info("Payment completed for order: {}", order.getId());
        return createPaymentStatusDto(order, "success", "Payment completed successfully");
    }

    // Get payment status
    public PaymentStatusDto getPaymentStatus(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        String status = order.getStatus() == OrderStatus.PAID ? "success" : "pending";
        return createPaymentStatusDto(order, status, "Status: " + order.getStatus());
    }

    private PaymentStatusDto createPaymentStatusDto(
            Order order, String status, String message) {
        return new PaymentStatusDto(
                message,
                order.getPaymentMethod(),
                order.getPaidAt(),
                order.getTotalPrice().multiply(new BigDecimal(100)).longValue(),
                order.getPaystackReference(),
                status,
                order.getId()
        );
    }

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

        return new OrderResponseDto(
                order.getId(),
                order.getUser().getId(),
                order.getCart().getId(),
                order.getStatus(),
                products,
                order.getTotalPrice(),
                order.getCreatedAt()
        );
    }
}