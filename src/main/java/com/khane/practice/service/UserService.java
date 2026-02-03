package com.khane.practice.service;

import com.khane.practice.dto.user.UserRequestDto;
import com.khane.practice.dto.user.UserResponseDto;
import com.khane.practice.dto.user.UserUpdateResponseDto;
import com.khane.practice.entity.user.User;
import com.khane.practice.exception.UserNotFoundException;
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


    public UserResponseDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

        return mapToUserResponse(user);
    }

    public UserResponseDto updateUser(UUID id, UserUpdateResponseDto userUpdateResponseDto) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

        // Update fields for user
        existingUser.setUsername(userUpdateResponseDto.getUsername());
        existingUser.setName(userUpdateResponseDto.getName());

        // Save the update
        User updatedUser = userRepository.save(existingUser);

        return mapToUserResponse(updatedUser);
    }

    public void deleteUser(UUID id) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

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