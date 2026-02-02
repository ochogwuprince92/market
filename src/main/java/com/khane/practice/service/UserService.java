package com.khane.practice.service;

import com.khane.practice.dto.user.UserRequestDto;
import com.khane.practice.dto.user.UserResponseDto;
import com.khane.practice.entity.user.User;
import com.khane.practice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDto createUser(UserRequestDto userRequestDto) {

        User user = new User();
        user.setName(userRequestDto.getName());
        user.setUsername(userRequestDto.getUsername());
        user.setEmail(userRequestDto.getEmail());

        User newUser = userRepository.save(user);

        return new UserResponseDto(
                newUser.getId(),
                newUser.getName(),
                newUser.getUsername(),
                newUser.getEmail()
        );
    }

    //    Use UserDTO instead of User directly
    public List<UserResponseDto> getAllUser() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .toList();
    }


    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    public User updateUser(UUID id, User user) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        // Update fields for user
        existingUser.setUsername(user.getUsername());
        existingUser.setName(user.getName());

        // Save the update
        return userRepository.save(existingUser);
    }

    public void deleteUser(UUID id) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(existingUser);
    }

    //    Map User to UserResponseDto
    private UserResponseDto mapToUserResponse(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getUsername()
        );


    }
}