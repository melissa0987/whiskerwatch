package com.example.whiskerwatch.demo.service;


import com.example.whiskerwatch.demo.model.Pet;
import com.example.whiskerwatch.demo.model.User;
import com.example.whiskerwatch.demo.model.PetType;
import com.example.whiskerwatch.demo.repository.PetJPARepository;
import com.example.whiskerwatch.demo.repository.UserJPARepository;
import com.example.whiskerwatch.demo.repository.PetTypeJPARepository;

import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PetService {
    private final PetJPARepository petRepository;
    private final UserJPARepository userRepository;
    private final PetTypeJPARepository petTypeRepository;

    public PetService(PetJPARepository petRepository,
                      UserJPARepository userRepository,
                      PetTypeJPARepository petTypeRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.petTypeRepository = petTypeRepository;
    }

    public List<Pet> getPets(Long ownerId, String petType, Boolean isActive) {
        if (ownerId != null) {
            return petRepository.findByOwnerId(ownerId);
        }
        if (petType != null && !petType.isBlank()) {
            return petRepository.findByTypeTypeName(petType);
        }
        if (isActive != null) {
            return petRepository.findByIsActive(isActive);
        }
        return petRepository.findAll();
    }

    public Optional<Pet> getPet(Long petId) {
        return petRepository.findById(petId);
    }

    public List<Pet> getPetsByOwner(Long ownerId) {
        return petRepository.findByOwnerId(ownerId);
    }

    public void createPet(@NonNull String name, Integer age, String breed, BigDecimal weight,
                          String specialInstructions, @NonNull Long ownerId, @NonNull Long typeId) {

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));

        PetType petType = petTypeRepository.findById(typeId)
                .orElseThrow(() -> new IllegalArgumentException("Pet type not found"));

        Pet pet = new Pet(name, age, breed, weight, specialInstructions, owner, petType);
        petRepository.save(pet);
    }

    public void updatePet(@NonNull Long petId, @NonNull String name, Integer age, String breed,
                          BigDecimal weight, String specialInstructions, @NonNull Long ownerId,
                          @NonNull Long typeId, Boolean isActive) {

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("Pet not found"));

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));

        PetType petType = petTypeRepository.findById(typeId)
                .orElseThrow(() -> new IllegalArgumentException("Pet type not found"));

        pet.setName(name);
        pet.setAge(age);
        pet.setBreed(breed);
        pet.setWeight(weight);
        pet.setSpecialInstructions(specialInstructions);
        pet.setOwner(owner);
        pet.setType(petType);
        if (isActive != null) {
            pet.setIsActive(isActive);
        }

        petRepository.save(pet);
    }

    public void deletePet(@NonNull Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("Pet not found"));
        petRepository.delete(pet);
    }

    public List<Pet> getActivePets() {
        return petRepository.findByIsActive(true);
    }

    public List<Pet> getPetsByType(String typeName) {
        return petRepository.findByTypeTypeName(typeName);
    }

    // Security helper method
    public boolean isPetOwner(Long petId, Long userId) {
        return petRepository.findById(petId)
                .map(pet -> pet.getOwner().getId().equals(userId))
                .orElse(false);
    }
}