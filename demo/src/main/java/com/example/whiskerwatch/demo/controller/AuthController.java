package com.example.whiskerwatch.demo.controller;

import com.example.whiskerwatch.demo.model.User;
import com.example.whiskerwatch.demo.service.UserService;
import com.example.whiskerwatch.demo.controller.response.UserResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        System.out.println("Login attempt - Email: " + email); // Debug log

        Optional<User> optionalUser = userService.getUserByEmail(email);
        if (optionalUser.isEmpty()) {
            System.out.println("User not found for email: " + email); // Debug log
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Invalid email or password"));
        }

        User user = optionalUser.get();
        System.out.println("User found - Stored password hash: " + user.getPassword()); // Debug log
        System.out.println("Input password: " + password); // Debug log
        
        // Use BCrypt to compare hashed password
        boolean passwordMatch = passwordEncoder.matches(password, user.getPassword());
        System.out.println("Password match result: " + passwordMatch); // Debug log
        
        if (!passwordMatch) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Invalid email or password"));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "user", UserResponse.toResponse(user)
        ));
    }
}