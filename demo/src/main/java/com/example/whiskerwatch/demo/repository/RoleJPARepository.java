package com.example.whiskerwatch.demo.repository;

import com.example.whiskerwatch.demo.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleJPARepository extends JpaRepository<Role, Long> {
    // Find role by name
    Optional<Role> findByRoleName(String roleName);

    // Check if role exists by name
    boolean existsByRoleName(String roleName);
}