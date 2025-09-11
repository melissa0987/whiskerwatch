package com.example.whiskerwatch.demo.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.whiskerwatch.demo.controller.request.CreateGroup;
import com.example.whiskerwatch.demo.controller.request.UpdateGroup;
import com.example.whiskerwatch.demo.controller.request.UserRequest;
import com.example.whiskerwatch.demo.controller.response.UserResponse;
import com.example.whiskerwatch.demo.model.User;
import com.example.whiskerwatch.demo.security.CustomUserDetailsService;
import com.example.whiskerwatch.demo.service.UserService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.example.whiskerwatch.demo.controller.request.PasswordChangeRequest; 
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Map;
import java.util.Optional;

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
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.userId == #userId")
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

    // Get all users - Admin only
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
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

    // Get user by ID - Admin or user themselves
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.userId == #userId")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        return userService.getUser(userId)
                .map(UserResponse::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get current user profile
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetailsService.CustomUserPrincipal) {
            CustomUserDetailsService.CustomUserPrincipal userPrincipal = 
                (CustomUserDetailsService.CustomUserPrincipal) authentication.getPrincipal();
            return ResponseEntity.ok(UserResponse.toResponse(userPrincipal.getUser()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Register (Signup) user - Public endpoint
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody @Validated(CreateGroup.class) UserRequest userRequest) {
        try {
            System.out.println("Creating user with data: " + userRequest);
            
            User savedUser = userService.createUser(
                    userRequest.getUsername(),
                    userRequest.getEmail(),
                    userRequest.getPassword(),
                    userRequest.getRoleId(),           // null is OK – defaults to CUSTOMER
                    userRequest.getCustomerTypeId(),
                    userRequest.getFirstName(),
                    userRequest.getLastName(),
                    userRequest.getPhoneNumber(),
                    userRequest.getAddress()
            );

            System.out.println("User created successfully with ID: " + savedUser.getId());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "success", true,
                            "message", "User registered successfully",
                            "userId", savedUser.getId()
                    ));
        } catch (IllegalArgumentException e) {
            System.err.println("User creation validation error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
            System.err.println("User creation error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to create user: " + e.getMessage()
                    ));
        }
    }

    
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.userId == #userId")
    public ResponseEntity<?> updateUser(
            @PathVariable Long userId,
            @RequestBody @Validated(UpdateGroup.class) UserRequest userRequest) {
        try {
            System.out.println("Updating user " + userId + " with data: " + userRequest);
            
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
            
            System.out.println("User " + userId + " updated successfully");
            
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "message", "User updated successfully"
            ));
        } catch (IllegalArgumentException e) {
            System.err.println("User update validation error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                        "success", false, 
                        "message", e.getMessage()
                    ));
        } catch (Exception e) {
            System.err.println("User update error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "success", false, 
                        "message", "Failed to update user: " + e.getMessage()
                    ));
        }
    }


    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.userId == #userId")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId, @RequestBody(required = false) Map<String, String> deleteRequest) {
        try {
            System.out.println("Delete request for user " + userId + " with data: " + deleteRequest);
            
            // Get current user from security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            // If it's the user deleting their own account, verify password
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetailsService.CustomUserPrincipal) {
                CustomUserDetailsService.CustomUserPrincipal userPrincipal = 
                    (CustomUserDetailsService.CustomUserPrincipal) authentication.getPrincipal();
                
                // If user is deleting their own account, require password verification
                if (userPrincipal.getUserId().equals(userId) && deleteRequest != null) {
                    String password = deleteRequest.get("password");
                    if (password != null && !password.isEmpty()) {
                        // Verify password
                        Optional<User> userOpt = userService.getUser(userId);
                        if (userOpt.isPresent()) {
                            User user = userOpt.get();
                            if (!passwordEncoder.matches(password, user.getPassword())) {
                                System.err.println("Invalid password for user deletion: " + userId);
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(Map.of("success", false, "message", "Invalid password"));
                            }
                        } else {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                    .body(Map.of("success", false, "message", "User not found"));
                        }
                    } else {
                        System.err.println("Password required for user deletion: " + userId);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("success", false, "message", "Password is required"));
                    }
                }
            }
            
            System.out.println("Proceeding with user deletion: " + userId);
            userService.deleteUser(userId);
            System.out.println("User " + userId + " deleted successfully");
            
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "message", "Account deleted successfully"
            ));
        } catch (IllegalArgumentException e) {
            System.err.println("User not found for deletion: " + userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "User not found"));
        } catch (Exception e) {
            System.err.println("User deletion error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to delete account"));
        }
    }
}