package com.example.whiskerwatch.demo.repository;


import com.example.whiskerwatch.demo.model.CustomerType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface CustomerTypeJPARepository extends JpaRepository<CustomerType, Long> {
    // Find customer type by name
    Optional<CustomerType> findByTypeName(String typeName);

    // Check if customer type exists by name
    boolean existsByTypeName(String typeName);
}