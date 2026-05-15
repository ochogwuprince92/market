package com.khane.market.dto.user;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.security.PrivateKey;
import java.util.UUID;

@AllArgsConstructor
@Data
public class UserResponseDto {

    private UUID id;
    private String name;
    private String username;
    private String email;
}
