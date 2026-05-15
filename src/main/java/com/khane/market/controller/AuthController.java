package com.khane.market.controller;

import com.khane.market.config.JwtUtil;
import com.khane.market.dto.auth.AuthResponse;
import com.khane.market.dto.auth.LoginRequest;
import com.khane.market.entity.user.User;
import com.khane.market.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * User login endpoint
     * POST /api/v1/auth/login
     *
     * Request:
     * {
     *   "email": "user@example.com",
     *   "password": "password123"
     * }
     *
     * Response:
     * {
     *   "token": "eyJhbGciOiJIUzUxMiJ9...",
     *   "email": "user@example.com",
     *   "message": "Login successful"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        try {
            // Find user by email
            User user = userService.findByEmail(request.getEmail());

            if (user == null) {
                log.warn("User not found: {}", request.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse(null, request.getEmail(), "Invalid email or password"));
            }

            // Verify password (simple check - enhance in production with bcrypt)
            if (user.getPassword() == null || !user.getPassword().equals(request.getPassword())) {
                log.warn("Invalid password for user: {}", request.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse(null, request.getEmail(), "Invalid email or password"));
            }

            // Generate JWT token
            String token = jwtUtil.generateToken(user.getId(), user.getEmail());

            log.info("Login successful for user: {}", request.getEmail());
            return ResponseEntity.ok(new AuthResponse(
                    token,
                    user.getEmail(),
                    "Login successful"
            ));

        } catch (Exception e) {
            log.error("Login error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(null, request.getEmail(), "Login failed"));
        }
    }

    /**
     * Health check endpoint
     * GET /api/v1/auth/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth service is running");
    }
}

