package com.example.whiskerwatch.demo.controller;

import java.util.List;
import java.util.Map;
import static java.util.Map.entry;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.whiskerwatch.demo.controller.response.UserResponse;
import com.example.whiskerwatch.demo.service.BookingService;
import com.example.whiskerwatch.demo.service.PetService;
import com.example.whiskerwatch.demo.service.UserService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
@PreAuthorize("hasRole('ADMIN')") 
public class AdminController {

    private final UserService userService;
    private final PetService petService;
    private final BookingService bookingService;

    public AdminController(UserService userService, PetService petService, BookingService bookingService) {
        this.userService = userService;
        this.petService = petService;
        this.bookingService = bookingService;
    }

    // ==================== USER MANAGEMENT ====================

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserResponse> users = userService.getUsers(null, null, null)
                    .stream()
                    .map(UserResponse::toResponse)
                    .toList();

            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", users
            ));
        } catch (Exception e) {
            System.err.println("Error fetching users: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to fetch users"
            ));
        }
    }

    @DeleteMapping("/bookings/{bookingId}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long bookingId) {
        try {
            bookingService.deleteBooking(bookingId);
            return ResponseEntity.ok(Map.of("success", true, "message", "Booking deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "success", false,
                "message", "Booking not found"
            ));
        } catch (Exception e) {
            System.err.println("Error deleting booking: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to delete booking"
            ));
        }
    }

    // ==================== STATISTICS ====================

    @GetMapping("/stats/overview")
    public ResponseEntity<?> getOverviewStats() {
        try {
            var allUsers = userService.getUsers(null, null, null);
            var allPets = petService.getPets(null, null, null);
            var allBookings = bookingService.getBookings(null, null, null, null, null);

            Map<String, Object> stats = Map.ofEntries(
                entry("totalUsers", allUsers.size()),
                entry("activeUsers", allUsers.stream().filter(u -> u.getIsActive()).count()),
                entry("totalPets", allPets.size()),
                entry("activePets", allPets.stream().filter(p -> p.getIsActive()).count()),
                entry("totalBookings", allBookings.size()),
                entry("pendingBookings", allBookings.stream().filter(b -> "PENDING".equals(b.getStatus().getStatusName())).count()),
                entry("completedBookings", allBookings.stream().filter(b -> "COMPLETED".equals(b.getStatus().getStatusName())).count()),
                entry("inProgressBookings", allBookings.stream().filter(b -> "IN_PROGRESS".equals(b.getStatus().getStatusName())).count()),
                entry("cancelledBookings", allBookings.stream().filter(b -> "CANCELLED".equals(b.getStatus().getStatusName())).count()),
                entry("owners", allUsers.stream().filter(u -> u.getCustomerType() != null && "OWNER".equals(u.getCustomerType().getTypeName())).count()),
                entry("sitters", allUsers.stream().filter(u -> u.getCustomerType() != null && "SITTER".equals(u.getCustomerType().getTypeName())).count())
            );

            return ResponseEntity.ok(Map.of("success", true, "data", stats));
        } catch (Exception e) {
            System.err.println("Error fetching overview stats: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to fetch statistics"
            ));
        }
    }

    @GetMapping("/stats/users")
    public ResponseEntity<?> getUserStats() {
        try {
            var allUsers = userService.getUsers(null, null, null);

            Map<String, Object> userStats = Map.of(
                    "total", allUsers.size(),
                    "active", allUsers.stream().filter(u -> u.getIsActive()).count(),
                    "inactive", allUsers.stream().filter(u -> !u.getIsActive()).count(),
                    "admins", allUsers.stream().filter(u -> "ADMIN".equals(u.getRole().getRoleName())).count(),
                    "customers", allUsers.stream().filter(u -> "CUSTOMER".equals(u.getRole().getRoleName())).count(),
                    "owners", allUsers.stream().filter(u -> u.getCustomerType() != null && "OWNER".equals(u.getCustomerType().getTypeName())).count(),
                    "sitters", allUsers.stream().filter(u -> u.getCustomerType() != null && "SITTER".equals(u.getCustomerType().getTypeName())).count()
            );

            return ResponseEntity.ok(Map.of("success", true, "data", userStats));
        } catch (Exception e) {
            System.err.println("Error fetching user stats: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to fetch user statistics"
            ));
        }
    }

    @GetMapping("/stats/bookings")
    public ResponseEntity<?> getBookingStats() {
        try {
            var allBookings = bookingService.getBookings(null, null, null, null, null);

            Map<String, Object> bookingStats = Map.of(
                    "total", allBookings.size(),
                    "pending", allBookings.stream().filter(b -> "PENDING".equals(b.getStatus().getStatusName())).count(),
                    "confirmed", allBookings.stream().filter(b -> "CONFIRMED".equals(b.getStatus().getStatusName())).count(),
                    "inProgress", allBookings.stream().filter(b -> "IN_PROGRESS".equals(b.getStatus().getStatusName())).count(),
                    "completed", allBookings.stream().filter(b -> "COMPLETED".equals(b.getStatus().getStatusName())).count(),
                    "cancelled", allBookings.stream().filter(b -> "CANCELLED".equals(b.getStatus().getStatusName())).count(),
                    "rejected", allBookings.stream().filter(b -> "REJECTED".equals(b.getStatus().getStatusName())).count()
            );

            return ResponseEntity.ok(Map.of("success", true, "data", bookingStats));
        } catch (Exception e) {
            System.err.println("Error fetching booking stats: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to fetch booking statistics"
            ));
        }
    }
}
