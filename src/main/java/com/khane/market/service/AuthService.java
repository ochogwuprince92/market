package com.khane.market.service;

import com.khane.market.dto.auth.AuthResponseDto;
import com.khane.market.dto.auth.RegisterRequestDto;
import com.khane.market.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

//    Register a new User
    public AuthResponseDto registerUser(RegisterRequestDto registerRequest){

    }
}
