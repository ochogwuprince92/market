package com.khane.market.controller;

import com.khane.market.dto.user.UserRequestDto;
import com.khane.market.dto.user.UserResponseDto;
import com.khane.market.dto.user.UserUpdateRequestDto;
import com.khane.market.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity <UserResponseDto> updateUser(@PathVariable UUID id,
                                            @RequestBody @Valid UserUpdateRequestDto userUpdateRequestDto){

        UserResponseDto updated = userService.updateUser(id, userUpdateRequestDto);
        return ResponseEntity.ok(updated);
    }

//    Delete User
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable UUID id){

        userService.deleteUser(id);

        return "User deleted successfully";

    }


}
