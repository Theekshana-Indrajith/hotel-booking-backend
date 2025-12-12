package com.hotel.bookingsystem.service;

import com.hotel.bookingsystem.model.User;
import com.hotel.bookingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Check if username already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        // Set defaults if not provided
        if (user.getRole() == null) {
            user.setRole("USER");
        }
        if (user.getEnabled() == null) {
            user.setEnabled(true);
        }

        return userRepository.save(user);
    }

    public User authenticateUser(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // For demo, compare plain text password
            // In production, use password encoder matches
            if (user.getPassword().equals(password) && user.getEnabled()) {
                return user;
            }
        }
        return null;
    }

    public boolean checkEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean checkUsernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}