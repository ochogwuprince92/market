package com.khane.practice.controller;

import com.khane.practice.entity.user.User;
import com.khane.practice.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository userRepository;

    //Call the constructor for the above
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    //Create a method for the user
    @PostMapping("create")
    public User createUser(@RequestBody User user){
        return userRepository.save(user);
    }

    @GetMapping
    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return userRepository.findById(id).orElseThrow(()-> new RuntimeException("User Not Found"));
    }

    @PutMapping("/{id}")
    public User updateUser (@PathVariable Long id, @RequestBody User updateUser){

        User existingUser = userRepository.findById(id).orElseThrow(()-> new RuntimeException("User Not Found"));

        //Update this fields for user
        existingUser.setName(updateUser.getName());
        existingUser.setUsername(updateUser.getUsername());

        return userRepository.save(existingUser);

    }
    @DeleteMapping("/{id}")

    //use String to return a value or void instead
    public String deleteUser (@PathVariable Long id){
        User user = userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User Not Found"));

        userRepository.delete(user);

        return "User deleted successfully.";
    }
}
