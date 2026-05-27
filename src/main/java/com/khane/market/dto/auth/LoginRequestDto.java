package com.khane.market.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {

    @NotBlank(message = "Email or phone number is required")
    private String identifier; // can be email or phone number

    @NotBlank(message = "Password is required")
    private String password;
}
