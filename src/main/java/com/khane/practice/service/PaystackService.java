package com.khane.practice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khane.practice.config.PaystackConfig;
import com.khane.practice.dto.payment.PaystackInitializeRequest;
import com.khane.practice.dto.payment.PaystackInitializeResponse;
import com.khane.practice.dto.payment.PaystackVerifyResponse;
import com.khane.practice.exception.PaymentInitializationException;
import com.khane.practice.exception.PaymentVerificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaystackService {

    private final PaystackConfig paystackConfig;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    private static final String INITIALIZE_URL = "/transaction/initialize";
    private static final String VERIFY_URL = "/transaction/verify/";

    /**
     * Initialize payment with Paystack
     */
    public PaystackInitializeResponse initializePayment(PaystackInitializeRequest request) {
        try {
            log.info("Initializing payment for order: {}", request.getOrderId());

            // Convert amount to kobo (Paystack uses kobo as the smallest unit)
            long amountInKobo = request.getAmount().multiply(new java.math.BigDecimal(100)).longValue();

            // Create request body
            String jsonRequest = objectMapper.writeValueAsString(new Object() {
                public final long amount = amountInKobo;
                public final String email = request.getEmail();
                public final String reference = generateReference(request.getOrderId());
                public final String callback_url = request.getCallbackUrl() != null ?
                        request.getCallbackUrl() : paystackConfig.getCallback().getUrl();
            });

            RequestBody body = RequestBody.create(jsonRequest, okhttp3.MediaType.get("application/json"));

            Request httpRequest = new Request.Builder()
                    .url(paystackConfig.getApi().getUrl() + INITIALIZE_URL)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + paystackConfig.getApi().getKey())
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = httpClient.newCall(httpRequest).execute()) {
                String responseBody = response.body().string();

                if (!response.isSuccessful()) {
                    log.error("Payment initialization failed with status: {}, response: {}",
                            response.code(), responseBody);
                    throw new PaymentInitializationException(
                            "Failed to initialize payment. Status: " + response.code());
                }

                JsonNode jsonResponse = objectMapper.readTree(responseBody);
                boolean status = jsonResponse.get("status").asBoolean();

                if (!status) {
                    String message = jsonResponse.get("message").asText();
                    log.error("Payment initialization failed: {}", message);
                    throw new PaymentInitializationException("Payment initialization failed: " + message);
                }

                JsonNode dataNode = jsonResponse.get("data");
                String reference = dataNode.get("reference").asText();
                String accessCode = dataNode.get("access_code").asText();
                String authorizationUrl = dataNode.get("authorization_url").asText();

                log.info("Payment initialized successfully. Reference: {}", reference);

                return new PaystackInitializeResponse(
                        request.getOrderId(),
                        reference,
                        accessCode,
                        authorizationUrl
                );
            }

        } catch (IOException e) {
            log.error("Error during payment initialization", e);
            throw new PaymentInitializationException("Payment initialization error: " + e.getMessage(), e);
        }
    }

    /**
     * Verify payment with Paystack
     */
    public PaystackVerifyResponse verifyPayment(String reference) {
        try {
            log.info("Verifying payment with reference: {}", reference);

            Request request = new Request.Builder()
                    .url(paystackConfig.getApi().getUrl() + VERIFY_URL + reference)
                    .get()
                    .addHeader("Authorization", "Bearer " + paystackConfig.getApi().getKey())
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body().string();

                if (!response.isSuccessful()) {
                    log.error("Payment verification failed with status: {}, response: {}",
                            response.code(), responseBody);
                    throw new PaymentVerificationException(
                            "Failed to verify payment. Status: " + response.code());
                }

                PaystackVerifyResponse verifyResponse = objectMapper.readValue(
                        responseBody, PaystackVerifyResponse.class);

                if (!verifyResponse.isStatus()) {
                    log.error("Payment verification failed: {}", verifyResponse.getMessage());
                    throw new PaymentVerificationException("Payment verification failed: " + verifyResponse.getMessage());
                }

                log.info("Payment verified successfully. Status: {}", verifyResponse.getData().getStatus());

                return verifyResponse;
            }

        } catch (IOException e) {
            log.error("Error during payment verification", e);
            throw new PaymentVerificationException("Payment verification error: " + e.getMessage(), e);
        }
    }

    /**
     * Validate webhook signature from Paystack
     */
    public boolean validateWebhookSignature(String signature, String payload) {
        try {
            String computedSignature = computeHmacSha512(
                    payload,
                    paystackConfig.getApi().getSecret()
            );

            boolean isValid = computedSignature.equals(signature);

            if (!isValid) {
                log.warn("Invalid webhook signature. Expected: {}, Got: {}", computedSignature, signature);
            }

            return isValid;
        } catch (Exception e) {
            log.error("Error validating webhook signature", e);
            return false;
        }
    }

    /**
     * Generate unique reference for payment
     */
    private String generateReference(UUID orderId) {
        return "ORD-" + orderId + "-" + System.currentTimeMillis();
    }

    /**
     * Compute HMAC-SHA512 signature
     */
    private String computeHmacSha512(String data, String secret) throws Exception {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA512");
        javax.crypto.spec.SecretKeySpec secretKeySpec =
                new javax.crypto.spec.SecretKeySpec(secret.getBytes(), "HmacSHA512");
        mac.init(secretKeySpec);
        byte[] hmacBytes = mac.doFinal(data.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : hmacBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }
}

