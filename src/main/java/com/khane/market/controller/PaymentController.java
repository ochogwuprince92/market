package com.khane.market.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khane.market.dto.payment.PaystackInitializeRequest;
import com.khane.market.dto.payment.PaystackInitializeResponse;
import com.khane.market.dto.payment.PaymentStatusDto;
import com.khane.market.dto.payment.WebhookPayload;
import com.khane.market.service.OrderService;
import com.khane.market.service.PaystackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final OrderService orderService;
    private final PaystackService paystackService;
    private final ObjectMapper objectMapper;

    /**
     * Initialize payment for an order
     * POST /api/v1/payments/initialize
     */
    @PostMapping("/initialize")
    public ResponseEntity<PaystackInitializeResponse> initializePayment(
            @Valid @RequestBody PaystackInitializeRequest request) {

        log.info("Payment initialization request for order: {}", request.getOrderId());

        try {
            PaystackInitializeResponse response = orderService.initiatePayment(
                    request.getOrderId(),
                    request.getEmail(),
                    request.getCallbackUrl()
            );

            log.info("Payment initialized successfully. Reference: {}", response.getPaystackReference());
            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            log.warn("Payment initialization failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(createErrorResponse(request.getOrderId(), false, e.getMessage()));

        } catch (Exception e) {
            log.error("Error initializing payment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse(request.getOrderId(), false, "Payment initialization failed"));
        }
    }

    /**
     * Verify payment manually
     * GET /api/v1/payments/verify/{reference}
     */
    @GetMapping("/verify/{reference}")
    public ResponseEntity<PaymentStatusDto> verifyPayment(
            @PathVariable String reference) {

        log.info("Payment verification request for reference: {}", reference);

        try {
            PaymentStatusDto status = orderService.completePayment(reference);
            return ResponseEntity.ok(status);

        } catch (Exception e) {
            log.error("Error verifying payment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get payment status for an order
     * GET /api/v1/payments/status/{orderId}
     */
    @GetMapping("/status/{orderId}")
    public ResponseEntity<PaymentStatusDto> getPaymentStatus(
            @PathVariable UUID orderId) {

        log.info("Payment status request for order: {}", orderId);

        try {
            PaymentStatusDto status = orderService.getPaymentStatus(orderId);
            return ResponseEntity.ok(status);

        } catch (Exception e) {
            log.error("Error getting payment status", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Paystack Webhook Handler
     * POST /api/v1/webhooks/paystack
     * Receives webhook from Paystack for successful payment
     */
    @PostMapping("/webhooks/paystack")
    public ResponseEntity<Map<String, String>> handlePaystackWebhook(
            @RequestHeader("X-Paystack-Signature") String signature,
            @RequestBody String payload) {

        log.info("Received Paystack webhook");

        try {
            // Validate webhook signature
            if (!paystackService.validateWebhookSignature(signature, payload)) {
                log.error("Invalid webhook signature");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("status", "invalid_signature"));
            }

            // Parse webhook payload
            WebhookPayload webhookData = objectMapper.readValue(payload, WebhookPayload.class);

            // Only process successful charge events
            if (!"charge.success".equals(webhookData.getEvent())) {
                log.info("Ignoring non-charge.success event: {}", webhookData.getEvent());
                return ResponseEntity.ok(Map.of("status", "ignored"));
            }

            String reference = webhookData.getData().getReference();
            log.info("Processing payment for reference: {}", reference);

            // Complete the payment
            PaymentStatusDto result = orderService.completePayment(reference);

            log.info("Webhook processed successfully for reference: {}", reference);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "reference", reference,
                    "message", "Payment verified and order updated"
            ));

        } catch (IOException e) {
            log.error("Error parsing webhook payload", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "Invalid payload"));

        } catch (Exception e) {
            log.error("Error processing webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }


    @GetMapping("/webhooks/paystack/health")
    public ResponseEntity<Map<String, String>> webhookHealth() {
        return ResponseEntity.ok(Map.of("status", "healthy", "message", "Webhook endpoint is operational"));
    }

    // Helper method to create error response
    private PaystackInitializeResponse createErrorResponse(UUID orderId, boolean success, String message) {
        PaystackInitializeResponse response = new PaystackInitializeResponse();
        response.setOrderId(orderId);
        response.setSuccess(success);
        response.setMessage(message);
        return response;
    }
}

