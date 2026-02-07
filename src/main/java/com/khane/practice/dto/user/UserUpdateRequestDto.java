package com.khane.practice.dto.user;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserUpdateRequestDto {

    @NotBlank(message = "name cannot be empty.")
    private String name;
    private String username;
}
