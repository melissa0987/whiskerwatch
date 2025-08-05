package com.example.whiskerwatch.demo.controller.response;

import com.example.whiskerwatch.demo.model.Pet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class PetResponse {
    private Long petId;
    private String name;
    private Integer age;
    private String breed;
    private BigDecimal weight;
    private String specialInstructions;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private String ownerName;
    private String ownerEmail;
    private String petTypeName;
    private int bookingsCount;

    public static PetResponse toResponse(Pet pet) {
        return new PetResponse(
                pet.getId(),
                pet.getName(),
                pet.getAge(),
                pet.getBreed(),
                pet.getWeight(),
                pet.getSpecialInstructions(),
                pet.getIsActive(),
                pet.getCreatedAt(),
                pet.getOwner() != null ? pet.getOwner().getFirstName() + " " + pet.getOwner().getLastName() : null,
                pet.getOwner() != null ? pet.getOwner().getEmail() : null,
                pet.getType() != null ? pet.getType().getTypeName() : null,
                pet.getBookings() != null ? pet.getBookings().size() : 0
        );
    }
}