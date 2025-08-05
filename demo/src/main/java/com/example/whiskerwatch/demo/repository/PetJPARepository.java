package com.example.whiskerwatch.demo.repository;

import com.example.whiskerwatch.demo.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PetJPARepository extends JpaRepository<Pet, Long> {
    // Find pets by owner ID
    List<Pet> findByOwnerId(Long ownerId);

    // Find pets by pet type name
    List<Pet> findByTypeTypeName(String typeName);

    // Find pets by active status
    List<Pet> findByIsActive(Boolean isActive);

    // Find pets by name containing (for search)
    List<Pet> findByNameContaining(String name);

    // Find pets by breed
    List<Pet> findByBreed(String breed);

    // Find pets by owner ID and active status
    List<Pet> findByOwnerIdAndIsActive(Long ownerId, Boolean isActive);

    // Find pets by age range
    List<Pet> findByAgeBetween(Integer minAge, Integer maxAge);

    // Custom query to find pets by owner's email
    @Query("SELECT p FROM Pet p WHERE p.owner.email = :ownerEmail")
    List<Pet> findByOwnerEmail(@Param("ownerEmail") String ownerEmail);

    // Custom query to find pets by owner's name
    @Query("SELECT p FROM Pet p WHERE p.owner.firstName LIKE %:firstName% AND p.owner.lastName LIKE %:lastName%")
    List<Pet> findByOwnerName(@Param("firstName") String firstName, @Param("lastName") String lastName);

    // Count pets by owner
    long countByOwnerId(Long ownerId);

    // Count active pets by owner
    long countByOwnerIdAndIsActive(Long ownerId, Boolean isActive);
}