package com.khane.practice.dto.user;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class UserUpdateResponseDto {

    @NotBlank(message = "name cannot be empty.")
    private String name;
    private String username;
}
