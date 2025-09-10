package com.example.whiskerwatch.demo.controller.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {

    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class})
    @Size(min = 3, max = 50, message = "username must be between 3 and 50 characters")
    private String username;

    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class})
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @NotBlank(groups = CreateGroup.class)
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(groups = {CreateGroup.class, UpdateGroup.class})
    private Long roleId;

    private Long customerTypeId;

    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class})
    @Size(max = 255, message = "First name must not exceed 255 characters")
    private String firstName;

    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class})
    @Size(max = 255, message = "Last name must not exceed 255 characters")
    private String lastName;

    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class})
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class})
    private String address;

    private Boolean isActive;
}