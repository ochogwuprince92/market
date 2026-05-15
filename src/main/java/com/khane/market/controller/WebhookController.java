package com.khane.market.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khane.market.dto.payment.PaymentStatusDto;
import com.khane.market.dto.payment.WebhookPayload;
import com.khane.market.service.OrderService;
import com.khane.market.service.PaystackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final OrderService orderService;
    private final PaystackService paystackService;
    private final ObjectMapper objectMapper;

    /**
     * Paystack Webhook Handler
     * POST /api/v1/webhooks/paystack
     * Receives webhook from Paystack for successful payment
     */
    @PostMapping("/paystack")
    public ResponseEntity<Map<String, String>> handlePaystackWebhook(
            @RequestHeader(value = "X-Paystack-Signature", required = false) String signature,
            @RequestBody String payload) {

        log.info("Received Paystack webhook");

        try {
            // Validate webhook signature if present
            // Note: In production, signature MUST be present
            if (signature != null && !signature.trim().isEmpty()) {
                if (!paystackService.validateWebhookSignature(signature, payload)) {
                    log.error("Invalid webhook signature");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Map.of("status", "invalid_signature", "message", "Invalid webhook signature"));
                }
                log.info("Webhook signature validated successfully");
            } else {
                // Log warning in production environments
                log.warn("X-Paystack-Signature header missing from webhook request. " +
                        "Ensure Paystack is configured to send webhooks to the correct URL.");
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
            orderService.completePayment(reference);

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

    /**
     * Webhook health check endpoint
     * GET /api/v1/webhooks/paystack/health
     */
    @GetMapping("/paystack/health")
    public ResponseEntity<Map<String, String>> webhookHealth() {
        log.info("Webhook health check requested");
        return ResponseEntity.ok(Map.of("status", "healthy", "message", "Webhook endpoint is operational"));
    }
}

