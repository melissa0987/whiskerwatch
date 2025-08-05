package com.example.whiskerwatch.demo.service;

import com.example.whiskerwatch.demo.model.User;
import com.example.whiskerwatch.demo.model.Role;
import com.example.whiskerwatch.demo.model.CustomerType;
import com.example.whiskerwatch.demo.repository.UserJPARepository;
import com.example.whiskerwatch.demo.repository.RoleJPARepository;
import com.example.whiskerwatch.demo.repository.BookingJPARepository;
import com.example.whiskerwatch.demo.repository.CustomerTypeJPARepository;

import lombok.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserJPARepository userRepository;
    private final RoleJPARepository roleRepository;
    private final CustomerTypeJPARepository customerTypeRepository;
    private final BookingJPARepository bookingRepository;

    public UserService(UserJPARepository userRepository,
                       RoleJPARepository roleRepository,
                       CustomerTypeJPARepository customerTypeRepository,
                       BookingJPARepository bookingRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.customerTypeRepository = customerTypeRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<User> getUsers(String email, String customerType, Boolean isActive) {
        if (email != null && !email.isBlank()) {
            return userRepository.findByEmailContaining(email);
        }
        if (customerType != null && !customerType.isBlank()) {
            return userRepository.findByCustomerTypeTypeName(customerType);
        }
        if (isActive != null) {
            return userRepository.findByIsActive(isActive);
        }
        return userRepository.findAll();
    }

    public Optional<User> getUser(Long userId) {
        return userRepository.findById(userId);
    }

    public void createUser(@NonNull String userName, @NonNull String email, @NonNull String password,
                           @NonNull Long roleId, Long customerTypeId, @NonNull String firstName,
                           @NonNull String lastName, @NonNull String phoneNumber, @NonNull String address) {

        // Check for existing constraints BEFORE attempting to save
        if (userRepository.existsByUserName(userName)) {
            throw new IllegalArgumentException("Username already exists: " + userName);
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Phone number already exists: " + phoneNumber);
        }

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        CustomerType customerType = null;
        if (customerTypeId != null) {
            customerType = customerTypeRepository.findById(customerTypeId)
                    .orElseThrow(() -> new IllegalArgumentException("Customer type not found"));
        }

        User user = new User(userName, email, password, role, firstName, lastName, phoneNumber, address);
        user.setCustomerType(customerType);

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            // Handle any remaining constraint violations
            if (e.getMessage().contains("users_pkey")) {
                throw new IllegalStateException("Primary key constraint violation. Database sequence may need reset.");
            } else if (e.getMessage().contains("user_name")) {
                throw new IllegalArgumentException("Username already exists");
            } else if (e.getMessage().contains("email")) {
                throw new IllegalArgumentException("Email already exists");
            } else if (e.getMessage().contains("phone_number")) {
                throw new IllegalArgumentException("Phone number already exists");
            }
            throw e; // Re-throw if not handled
        }
    }

    public void updateUser(@NonNull Long userId, @NonNull String userName, @NonNull String email,
                           String password, @NonNull Long roleId, Long customerTypeId,
                           @NonNull String firstName, @NonNull String lastName,
                           @NonNull String phoneNumber, @NonNull String address, Boolean isActive) {

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check for conflicts with other users (exclude current user)
        userRepository.findByUserName(userName)
                .filter(user -> !user.getId().equals(userId))
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Username already exists: " + userName);
                });

        userRepository.findByEmail(email)
                .filter(user -> !user.getId().equals(userId))
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Email already exists: " + email);
                });

        userRepository.findByPhoneNumber(phoneNumber)
                .filter(user -> !user.getId().equals(userId))
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Phone number already exists: " + phoneNumber);
                });

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        CustomerType customerType = null;
        if (customerTypeId != null) {
            customerType = customerTypeRepository.findById(customerTypeId)
                    .orElseThrow(() -> new IllegalArgumentException("Customer type not found"));
        }

        existingUser.setUserName(userName);
        existingUser.setEmail(email);
        if (password != null && !password.isBlank()) {
            existingUser.setPassword(password);
        }
        existingUser.setRole(role);
        existingUser.setCustomerType(customerType);
        existingUser.setFirstName(firstName);
        existingUser.setLastName(lastName);
        existingUser.setPhoneNumber(phoneNumber);
        existingUser.setAddress(address);
        if (isActive != null) {
            existingUser.setIsActive(isActive);
        }

        userRepository.save(existingUser);
    }

    @Transactional
    public void deleteUser(Long userId) {
        // First check if user exists
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }

        // Delete all bookings associated with this user (as owner or sitter)
        bookingRepository.deleteByUserId(userId);

        // Then delete the user
        userRepository.deleteById(userId);
    }

    public List<User> getUsersByRole(String roleName) {
        return userRepository.findByRoleRoleName(roleName);
    }

    public List<User> getActiveUsers() {
        return userRepository.findByIsActive(true);
    }
}