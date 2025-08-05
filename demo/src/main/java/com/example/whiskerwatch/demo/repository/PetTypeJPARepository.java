package com.example.whiskerwatch.demo.repository;

import com.example.whiskerwatch.demo.model.PetType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface PetTypeJPARepository extends JpaRepository<PetType, Long> {
    // Find pet type by name
    Optional<PetType> findByTypeName(String typeName);

    // Check if pet type exists by name
    boolean existsByTypeName(String typeName);
}