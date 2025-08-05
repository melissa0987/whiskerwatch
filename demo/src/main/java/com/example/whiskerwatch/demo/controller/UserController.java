package com.example.whiskerwatch.demo.controller;


import com.example.whiskerwatch.demo.controller.request.CreateGroup;
import com.example.whiskerwatch.demo.controller.request.UpdateGroup;
import com.example.whiskerwatch.demo.controller.request.UserRequest;
import com.example.whiskerwatch.demo.controller.response.UserResponse;
import com.example.whiskerwatch.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers(
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "customerType", required = false) String customerType,
            @RequestParam(name = "isActive", required = false) Boolean isActive) {
        return ResponseEntity.ok(userService.getUsers(email, customerType, isActive)
                .stream()
                .map(UserResponse::toResponse)
                .toList());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUser(userId)
                .map(UserResponse::toResponse)
                .orElse(null));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody @Validated(CreateGroup.class) UserRequest userRequest) {
        userService.createUser(
                userRequest.getUserName(),
                userRequest.getEmail(),
                userRequest.getPassword(),
                userRequest.getRoleId(),
                userRequest.getCustomerTypeId(),
                userRequest.getFirstName(),
                userRequest.getLastName(),
                userRequest.getPhoneNumber(),
                userRequest.getAddress()
        );
    }

    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateUser(@PathVariable Long userId,
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
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}