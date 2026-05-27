package com.khane.market.service;

import com.khane.market.config.JwtUtil;
import com.khane.market.dto.auth.*;
import com.khane.market.entity.user.User;
import com.khane.market.entity.user.UserRole;
import com.khane.market.repository.UserRepository;
import com.khane.market.util.TokenGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    /**
     * Register a new user with email verification
     */
    public AuthResponseDto registerUser(RegisterRequestDto request) {
        // Validate password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Check if phone number already exists
        if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new RuntimeException("Phone number already registered");
        }

        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        // Generate email verification token
        String verificationToken = TokenGenerator.generateToken();
        LocalDateTime tokenExpiry = LocalDateTime.now().plusHours(24);

        // Build new user
        User user = new User();
        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmailVerified(false);
        user.setEmailVerificationToken(verificationToken);
        user.setEmailVerificationTokenExpiry(tokenExpiry);

        // Set role (default to CUSTOMER if not specified)
        UserRole role = UserRole.CUSTOMER;
        if (request.getRole() != null && !request.getRole().isEmpty()) {
            try {
                role = UserRole.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid role provided: {}, defaulting to CUSTOMER", request.getRole());
            }
        }
        user.setRoles(Set.of(role));

        User savedUser = userRepository.save(user);
        log.info("New user registered: {}", savedUser.getEmail());

        // Send verification email
        emailService.sendVerificationEmail(savedUser.getEmail(), verificationToken);

        // Return response (no token yet, user must verify email first)
        return new AuthResponseDto(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getPhoneNumber(),
                savedUser.getName(),
                savedUser.getRoles(),
                null,
                "Registration successful. Please check your email to verify your account."
        );
    }

    /**
     * Verify email via token
     */
    public AuthResponseDto verifyEmail(String token) {
        User user = userRepository.findAll().stream()
                .filter(u -> u.getEmailVerificationToken() != null && u.getEmailVerificationToken().equals(token))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        // Check if token has expired
        if (user.getEmailVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification token has expired");
        }

        // Mark email as verified
        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiry(null);
        User savedUser = userRepository.save(user);

        log.info("Email verified for user: {}", savedUser.getEmail());

        // Generate token after verification
        Set<String> roles = savedUser.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
        String jwtToken = jwtUtil.generateToken(savedUser.getId(), savedUser.getEmail(), roles);

        return new AuthResponseDto(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getPhoneNumber(),
                savedUser.getName(),
                savedUser.getRoles(),
                jwtToken,
                "Email verified successfully"
        );
    }

    /**
     * Login user with email or phone number
     */
    public AuthResponseDto loginUser(LoginRequestDto request) {
        // Determine if identifier is email or phone number
        User user;
        if (request.getIdentifier().contains("@")) {
            // Email login
            user = userRepository.findByEmail(request.getIdentifier())
                    .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        } else {
            // Phone login
            user = userRepository.findByPhoneNumber(request.getIdentifier())
                    .orElseThrow(() -> new RuntimeException("Invalid phone or password"));
        }

        // Check if email is verified
        if (!user.getEmailVerified()) {
            throw new RuntimeException("Please verify your email first");
        }

        // Verify password with bcrypt
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        log.info("User logged in: {}", user.getEmail());

        // Generate token with roles
        Set<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), roles);

        return new AuthResponseDto(
                user.getId(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getName(),
                user.getRoles(),
                token,
                "Login successful"
        );
    }

    /**
     * Initiate forgot password flow
     */
    public AuthResponseDto forgotPassword(ForgotPasswordRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email not found"));

        // Generate reset token
        String resetToken = TokenGenerator.generateToken();
        LocalDateTime tokenExpiry = LocalDateTime.now().plusHours(1); // 1 hour expiry

        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiry(tokenExpiry);
        userRepository.save(user);

        log.info("Password reset token generated for: {}", user.getEmail());

        // Send reset email
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);

        return new AuthResponseDto(
                user.getId(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getName(),
                user.getRoles(),
                null,
                "Password reset link sent to your email"
        );
    }

    /**
     * Reset password with token
     */
    public AuthResponseDto resetPassword(ResetPasswordRequestDto request) {
        // Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // Find user by reset token
        User user = userRepository.findAll().stream()
                .filter(u -> u.getPasswordResetToken() != null && u.getPasswordResetToken().equals(request.getToken()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid password reset token"));

        // Check if token has expired
        if (user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Password reset token has expired");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        User savedUser = userRepository.save(user);

        log.info("Password reset successful for: {}", savedUser.getEmail());

        // Generate token for immediate login
        Set<String> roles = savedUser.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
        String token = jwtUtil.generateToken(savedUser.getId(), savedUser.getEmail(), roles);

        return new AuthResponseDto(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getPhoneNumber(),
                savedUser.getName(),
                savedUser.getRoles(),
                token,
                "Password reset successful"
        );
    }
}

