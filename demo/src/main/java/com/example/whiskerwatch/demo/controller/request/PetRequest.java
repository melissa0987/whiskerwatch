package com.example.whiskerwatch.demo.controller.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PetRequest {

    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class})
    @Size(min = 1, max = 100, message = "Pet name must be between 1 and 100 characters")
    private String name;

    @Min(value = 0, message = "Age must be non-negative")
    private Integer age;

    @Size(max = 100, message = "Breed must not exceed 100 characters")
    private String breed;

    @Min(value = 0, message = "Weight must be non-negative")
    private BigDecimal weight;

    private String specialInstructions;

    @NotNull(groups = {CreateGroup.class, UpdateGroup.class})
    private Long ownerId;

    @NotNull(groups = {CreateGroup.class, UpdateGroup.class})
    private Long typeId;

    private Boolean isActive;
}