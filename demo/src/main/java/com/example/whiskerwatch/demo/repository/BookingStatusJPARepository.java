package com.example.whiskerwatch.demo.repository;


import com.example.whiskerwatch.demo.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface BookingStatusJPARepository extends JpaRepository<BookingStatus, Long> {
    // Find booking status by name
    Optional<BookingStatus> findByStatusName(String statusName);

    // Check if booking status exists by name
    boolean existsByStatusName(String statusName);
}