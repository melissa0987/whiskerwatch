package com.example.whiskerwatch.demo.controller.response;

import com.example.whiskerwatch.demo.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String username;
    private String email;
    private String roleName;
    private String customerTypeName;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int ownedPetsCount;
    private int ownerBookingsCount;
    private int sitterBookingsCount;

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole() != null ? user.getRole().getRoleName() : null,
                user.getCustomerType() != null ? user.getCustomerType().getTypeName() : null,
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getIsActive(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getOwnedPets() != null ? user.getOwnedPets().size() : 0,
                user.getOwnerBookings() != null ? user.getOwnerBookings().size() : 0,
                user.getSitterBookings() != null ? user.getSitterBookings().size() : 0
        );
    }
}