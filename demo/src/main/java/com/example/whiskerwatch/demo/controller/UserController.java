package com.example.whiskerwatch.demo.controller;

import java.util.List;
import java.util.Map;

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

@Validated
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ✅ Get all users
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

    // ✅ Get user by ID
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        return userService.getUser(userId)
                .map(UserResponse::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Register (Signup) user
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody @Validated(CreateGroup.class) UserRequest userRequest) {
        User savedUser = userService.createUser(
                userRequest.getUserName(),
                userRequest.getEmail(),
                userRequest.getPassword(),
                userRequest.getRoleId(),           // null is OK → defaults to CUSTOMER
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

    // ✅ Update user
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long userId,
            @RequestBody @Validated(UpdateGroup.class) UserRequest userRequest) {
        userService.updateUser(
                userId,
                userRequest.getUserName(),
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

    // ✅ Delete user
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
