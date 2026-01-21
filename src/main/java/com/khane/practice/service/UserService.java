package com.khane.practice.service;

import com.khane.practice.entity.user.User;
import com.khane.practice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public User createUser(User user){
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    public User updateUser(Long id, User user) {

        User existingUser = userRepository.findById(id).orElseThrow(()-> new RuntimeException("User Not Found"));

    // Update fields for user
        existingUser.setUsername(user.getUsername());
        existingUser.setName(user.getName());

    // Save the update
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User not found"));

        userRepository.delete(existingUser);
    }
}
