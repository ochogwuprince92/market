package com.khane.market.service;

import com.khane.market.config.JwtUtil;
import com.khane.market.dto.auth.AuthResponseDto;
import com.khane.market.dto.auth.LoginRequestDto;
import com.khane.market.dto.auth.RegisterRequestDto;
import com.khane.market.entity.user.User;
import com.khane.market.entity.user.UserRole;
import com.khane.market.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponseDto registerUser(RegisterRequestDto request) {

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        // Build new user
        User user = new User();
        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(UserRole.CUSTOMER)); // default role

        User savedUser = userRepository.save(user);
        log.info("New user registered: {}", savedUser.getEmail());

        // Generate token
        Set<String> roles = savedUser.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        String token = jwtUtil.generateToken(savedUser.getId(), savedUser.getEmail(), roles);

        return new AuthResponseDto(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getRoles(),
                token,
                "Registration successful"
        );
    }

    public AuthResponseDto loginUser(LoginRequestDto request) {

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

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
                user.getName(),
                user.getRoles(),
                token,
                "Login successful"
        );
    }
}