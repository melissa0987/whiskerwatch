package com.example.whiskerwatch.demo.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.whiskerwatch.demo.controller.request.CreateGroup;
import com.example.whiskerwatch.demo.controller.request.UpdateGroup;
import com.example.whiskerwatch.demo.controller.request.UserRequest;
import com.example.whiskerwatch.demo.controller.response.UserResponse;
import com.example.whiskerwatch.demo.model.User;
import com.example.whiskerwatch.demo.service.UserService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.example.whiskerwatch.demo.controller.request.PasswordChangeRequest;

@Validated
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<?> changePassword(
            @PathVariable Long userId,
            @RequestBody @Validated PasswordChangeRequest passwordRequest) {
        
        try {
            Optional<User> userOpt = userService.getUser(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            User user = userOpt.get();
            
            // Verify current password
            if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Current password is incorrect"));
            }
            
            // Hash new password
            String hashedNewPassword = passwordEncoder.encode(passwordRequest.getNewPassword());
            
            // Update password in database
            userService.updatePassword(userId, hashedNewPassword);
            
            return ResponseEntity.ok(Map.of("success", true, "message", "Password updated successfully"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to update password"));
        }
    }

    //Get all users
    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers(
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "customerType", required = false) String customerType,
            @RequestParam(name = "isActive", required = false) Boolean isActive) {
        return ResponseEntity.ok(
                userService.getUsers(email, customerType, isActive)
                        .stream()
                        .map(UserResponse::toResponse)
                        .toList()
        );
    }

    // âœ… Get user by ID
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        return userService.getUser(userId)
                .map(UserResponse::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // âœ… Register (Signup) user
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody @Validated(CreateGroup.class) UserRequest userRequest) {
        User savedUser = userService.createUser(
                userRequest.getUsername(),
                userRequest.getEmail(),
                userRequest.getPassword(),
                userRequest.getRoleId(),           // null is OK â†’ defaults to CUSTOMER
                userRequest.getCustomerTypeId(),
                userRequest.getFirstName(),
                userRequest.getLastName(),
                userRequest.getPhoneNumber(),
                userRequest.getAddress()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "User registered successfully",
                        "userId", savedUser.getId()
                ));
    }

    // âœ… Update user
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long userId,
            @RequestBody @Validated(UpdateGroup.class) UserRequest userRequest) {
        userService.updateUser(
                userId,
                userRequest.getUsername(),
                userRequest.getEmail(),
                userRequest.getPassword(),
                userRequest.getRoleId(),
                userRequest.getCustomerTypeId(),
                userRequest.getFirstName(),
                userRequest.getLastName(),
                userRequest.getPhoneNumber(),
                userRequest.getAddress(),
                userRequest.getIsActive()
        );
        return ResponseEntity.ok(Map.of("message", "User updated successfully"));
    }

    // âœ… Delete user
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        var userOpt = userService.getUserByEmail(email);
        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of("message", "Invalid credentials"));
        }

        return ResponseEntity.ok(Map.of(
            "message", "Login successful",
            "userId", userOpt.get().getId(),
            "role", userOpt.get().getRole().getRoleName()
        ));
    }
}