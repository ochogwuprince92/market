package com.khane.market.dto.user;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class UserRequestDto {

    private String name;
    private String username;
    private String email;
}
