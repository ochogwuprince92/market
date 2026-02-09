package com.khane.practice.controller;

import com.khane.practice.dto.order.OrderRequestDto;
import com.khane.practice.dto.order.OrderResponseDto;
import com.khane.practice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Create a new order for a user
    @PostMapping("/user/{userId}")
    public ResponseEntity<OrderResponseDto> createOrder(
            @PathVariable UUID userId,
            @RequestBody @Valid OrderRequestDto orderRequestDto) {

        OrderResponseDto orderResponse = orderService.createOrder(userId, orderRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }
    // Get a specific order by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable UUID orderId) {

        OrderResponseDto orderResponse = orderService.getOrderById(orderId);
        return ResponseEntity.ok(orderResponse);
    }

    // Get all orders for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByUser(@PathVariable UUID userId) {

        List<OrderResponseDto> orders = orderService.getOrdersByUser(userId);
        return ResponseEntity.ok(orders);
    }

    // Update order status
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestParam String status) {

        OrderResponseDto updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }
}
