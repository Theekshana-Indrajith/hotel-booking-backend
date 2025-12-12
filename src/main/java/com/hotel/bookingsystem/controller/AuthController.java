package com.hotel.bookingsystem.controller;

import com.hotel.bookingsystem.model.User;
import com.hotel.bookingsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "${spring.mvc.cors.allowed-origins}")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            // Ensure enabled is set to true if null
            if (user.getEnabled() == null) {
                user.setEnabled(true);
            }
            // Ensure role is set to USER if null
            if (user.getRole() == null) {
                user.setRole("USER");
            }

            User registeredUser = userService.registerUser(user);
            return ResponseEntity.ok(registeredUser);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        User user = userService.authenticateUser(email, password);
        if (user != null) {
            // Create response with user info (excluding password)
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("email", user.getEmail());
            response.put("username", user.getUsername());
            response.put("fullName", user.getFullName());
            response.put("role", user.getRole());
            response.put("phone", user.getPhone());
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @GetMapping("/check-email/{email}")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        boolean exists = userService.checkEmailExists(email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/check-username/{username}")
    public ResponseEntity<Boolean> checkUsernameExists(@PathVariable String username) {
        boolean exists = userService.checkUsernameExists(username);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        try {
            return userService.getUserById(id)
                    .map(user -> {
                        Map<String, Object> response = new HashMap<>();
                        response.put("id", user.getId());
                        response.put("email", user.getEmail());
                        response.put("username", user.getUsername());
                        response.put("fullName", user.getFullName());
                        response.put("role", user.getRole());
                        response.put("phone", user.getPhone());
                        return ResponseEntity.ok(response);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}