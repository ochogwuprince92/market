package com.khane.practice.service;

import com.khane.practice.dto.order.OrderRequestDto;
import com.khane.practice.dto.order.OrderResponseDto;
import com.khane.practice.dto.payment.PaystackInitializeRequest;
import com.khane.practice.dto.payment.PaystackInitializeResponse;
import com.khane.practice.dto.payment.PaymentStatusDto;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    public OrderResponseDto createOrder(UUID userId, OrderRequestDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Cart cart = cartRepository.findById(dto.getCartId())
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));

        BigDecimal totalPrice = cart.getProducts()
                .stream()
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setUser(user);
        order.setCart(cart);
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.NEW);
        order.setCreatedAt(LocalDateTime.now());

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
    public OrderResponseDto updateOrderStatus(UUID orderId, OrderStatus status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);

        return mapToOrderResponse(updatedOrder);
    }

    // Initiate payment for an order
    public PaystackInitializeResponse initiatePayment(UUID orderId, String userEmail, String callbackUrl) {
        log.info("Initiating payment for order: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        // Check if payment is already initiated
        if (order.getPaystackReference() != null) {
            log.warn("Payment already initiated for order: {}", orderId);
            throw new IllegalStateException("Payment has already been initiated for this order");
        }

        // Create payment request
        PaystackInitializeRequest request = new PaystackInitializeRequest();
        request.setOrderId(orderId);
        request.setAmount(order.getTotalPrice());
        request.setEmail(userEmail);
        request.setCallbackUrl(callbackUrl);

        // Initialize payment with Paystack
        PaystackInitializeResponse response = paystackService.initializePayment(request);

        // Update order with payment details
        order.setPaystackReference(response.getPaystackReference());
        order.setPaystackAccessCode(response.getAccessCode());
        order.setAuthorizationUrl(response.getAuthorizationUrl());
        order.setStatus(OrderStatus.PENDING);

        orderRepository.save(order);

        log.info("Payment initiated successfully for order: {}", orderId);
        return response;
    }

    // Complete payment (called from webhook or manual verification)
    public PaymentStatusDto completePayment(String paystackReference) {
        log.info("Completing payment with reference: {}", paystackReference);

        Order order = orderRepository.findByPaystackReference(paystackReference)
                .orElseThrow(() -> new OrderNotFoundException("Order not found for reference: " + paystackReference));

        // Verify payment with Paystack
        var verifyResponse = paystackService.verifyPayment(paystackReference);

        if (!"success".equalsIgnoreCase(verifyResponse.getData().getStatus())) {
            log.warn("Payment verification failed for reference: {}", paystackReference);
            return createPaymentStatusDto(order, "failed", "Payment was not successful");
        }

        // Update order status to PAID
        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        order.setPaymentMethod("Paystack");

        orderRepository.save(order);

        log.info("Payment completed successfully for order: {}", order.getId());
        return createPaymentStatusDto(order, "success", "Payment completed successfully");
    }

    // Get payment status
    public PaymentStatusDto getPaymentStatus(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        String status = order.getStatus() == OrderStatus.PAID ? "success" : "pending";
        return createPaymentStatusDto(order, status, "Status: " + order.getStatus());
    }

    // Helper method to create PaymentStatusDto
    private PaymentStatusDto createPaymentStatusDto(Order order, String status, String message) {
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
        // Use totalPrice directly from order
        BigDecimal totalPrice = order.getTotalPrice();

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
