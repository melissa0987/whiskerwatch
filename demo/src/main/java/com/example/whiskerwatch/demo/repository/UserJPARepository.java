package com.example.whiskerwatch.demo.repository;


import com.example.whiskerwatch.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserJPARepository extends JpaRepository<User, Long> {
    // Find by username
    Optional<User> findByUserName(String userName);

    // Find by email
    Optional<User> findByEmail(String email);

    // Find users by email containing (for search functionality)
    List<User> findByEmailContaining(String email);

    // Find by role name
    List<User> findByRoleRoleName(String roleName);

    // Find by customer type name
    List<User> findByCustomerTypeTypeName(String customerTypeName);

    // Find by active status
    List<User> findByIsActive(Boolean isActive);

    // Find by phone number
    Optional<User> findByPhoneNumber(String phoneNumber);

    // Custom query to find users by first and last name
    @Query("SELECT u FROM User u WHERE u.firstName LIKE %:firstName% AND u.lastName LIKE %:lastName%")
    List<User> findByFirstNameAndLastNameContaining(@Param("firstName") String firstName,
                                                    @Param("lastName") String lastName);

    // Delete by email (custom query)
    @Modifying
    @Query("DELETE FROM User u WHERE u.email = :email")
    void deleteByEmail(@Param("email") String email);

    // Check if username exists
    boolean existsByUserName(String userName);

    // Check if email exists
    boolean existsByEmail(String email);

    // Check if phone number exists
    boolean existsByPhoneNumber(String phoneNumber);
}
