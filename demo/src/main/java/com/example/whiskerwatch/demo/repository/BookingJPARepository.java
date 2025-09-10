package com.example.whiskerwatch.demo.repository;

import com.example.whiskerwatch.demo.model.Booking;
import com.example.whiskerwatch.demo.model.BookingStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookingJPARepository extends JpaRepository<Booking, Long> {
    // Find bookings by owner ID
    List<Booking> findByOwnerId(Long ownerId);

    // Find bookings by sitter ID
    List<Booking> findBySitterId(Long sitterId);

    // Find bookings by pet ID
    List<Booking> findByPetId(Long petId);

    // Find bookings by status name
    List<Booking> findByStatus(BookingStatus status);

    // Find bookings by booking date
    List<Booking> findByBookingDate(LocalDate bookingDate);

    // Find bookings between date range
    List<Booking> findByBookingDateBetween(LocalDate startDate, LocalDate endDate);

    // Find bookings by sitter and date
    List<Booking> findBySitterIdAndBookingDate(Long sitterId, LocalDate bookingDate);

    // Find bookings by owner and date range
    List<Booking> findByOwnerIdAndBookingDateBetween(Long ownerId, LocalDate startDate, LocalDate endDate);

    // Custom query to find overlapping time slots for a sitter on a specific date
    @Query("SELECT b FROM Booking b WHERE b.sitter.id = :sitterId AND b.bookingDate = :date " +
            "AND ((b.startTime <= :endTime AND b.endTime >= :startTime))")
    List<Booking> findBySitterIdAndBookingDateAndTimeOverlap(@Param("sitterId") Long sitterId,
                                                             @Param("date") LocalDate date,
                                                             @Param("startTime") LocalTime startTime,
                                                             @Param("endTime") LocalTime endTime);

    // Find upcoming bookings for a sitter
    @Query("SELECT b FROM Booking b WHERE b.sitter.id = :sitterId AND b.bookingDate >= :currentDate ORDER BY b.bookingDate, b.startTime")
    List<Booking> findUpcomingBookingsBySitter(@Param("sitterId") Long sitterId, @Param("currentDate") LocalDate currentDate);

    // Find upcoming bookings for an owner
    @Query("SELECT b FROM Booking b WHERE b.owner.id = :ownerId AND b.bookingDate >= :currentDate ORDER BY b.bookingDate, b.startTime")
    List<Booking> findUpcomingBookingsByOwner(@Param("ownerId") Long ownerId, @Param("currentDate") LocalDate currentDate);

    // Find bookings by pet and date range
    List<Booking> findByPetIdAndBookingDateBetween(Long petId, LocalDate startDate, LocalDate endDate);

    // Count bookings by status
    long countByStatusStatusName(String statusName);

    // Count bookings by sitter
    long countBySitterId(Long sitterId);

    // Count bookings by owner
    long countByOwnerId(Long ownerId);

    // Find bookings by multiple criteria
    @Query("SELECT b FROM Booking b WHERE " +
            "(:ownerId IS NULL OR b.owner.id = :ownerId) AND " +
            "(:sitterId IS NULL OR b.sitter.id = :sitterId) AND " +
            "(:petId IS NULL OR b.pet.id = :petId) AND " +
            "(:statusName IS NULL OR b.status.statusName = :statusName) AND " +
            "(:bookingDate IS NULL OR b.bookingDate = :bookingDate)")
    List<Booking> findBookingsByCriteria(@Param("ownerId") Long ownerId,
                                         @Param("sitterId") Long sitterId,
                                         @Param("petId") Long petId,
                                         @Param("statusName") String statusName,
                                         @Param("bookingDate") LocalDate bookingDate);

    // NEW: Delete methods for user deletion
    @Modifying
    @Query("DELETE FROM Booking b WHERE b.owner.id = :userId")
    void deleteByOwnerId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Booking b WHERE b.sitter.id = :userId")
    void deleteBySitterId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Booking b WHERE b.owner.id = :userId OR b.sitter.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}