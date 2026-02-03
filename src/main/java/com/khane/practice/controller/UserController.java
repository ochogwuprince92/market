package com.khane.practice.controller;

import com.khane.practice.dto.user.UserRequestDto;
import com.khane.practice.dto.user.UserResponseDto;
import com.khane.practice.dto.user.UserUpdateResponseDto;
import com.khane.practice.entity.user.User;
import com.khane.practice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService  userService;

    @PostMapping("/create")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody @Valid UserRequestDto userRequestDto){

        UserResponseDto userResponseDto = userService.createUser(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userResponseDto);
    }

    @GetMapping
    public List<UserResponseDto> getAllUser(){
        return userService.getAllUser();
    }

    @GetMapping("/{id}")
    public UserResponseDto getUserById(@PathVariable UUID id){
        return userService.getUserById(id);
    }

//    Update User
    @PutMapping("/{id}")
    public UserResponseDto updateUser(@PathVariable UUID id,
                                            @RequestBody @Valid UserUpdateResponseDto userUpdateResponseDto){
        return userService.updateUser(id, userUpdateResponseDto);
    }

//    Delete User
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable UUID id){

        userService.deleteUser(id);

        return "User deleted successfully";

    }


}
