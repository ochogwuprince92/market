package com.khane.market.dto.auth;

import com.khane.market.entity.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AuthResponseDto {

   private UUID id;
   private String email;
   private String password;
   private Set<UserRole> roles;
}
 