package com.khane.practice.repository;

import com.khane.practice.dto.user.UserRequestDto;
import com.khane.practice.dto.user.UserResponseDto;
import com.khane.practice.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

//    UserResponseDto createUser(UserRequestDto userRequestDto);
}
