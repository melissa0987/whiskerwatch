package com.example.whiskerwatch.demo.service;
import com.example.whiskerwatch.demo.model.Booking;
import com.example.whiskerwatch.demo.model.Pet;
import com.example.whiskerwatch.demo.model.User;
import com.example.whiskerwatch.demo.model.BookingStatus;
import com.example.whiskerwatch.demo.repository.BookingJPARepository;
import com.example.whiskerwatch.demo.repository.PetJPARepository;
import com.example.whiskerwatch.demo.repository.UserJPARepository;
import com.example.whiskerwatch.demo.repository.BookingStatusJPARepository;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    private final BookingJPARepository bookingRepository;
    private final PetJPARepository petRepository;
    private final UserJPARepository userRepository;
    private final BookingStatusJPARepository bookingStatusRepository;
    

    public BookingService(BookingJPARepository bookingRepository,
                          PetJPARepository petRepository,
                          UserJPARepository userRepository,
                          BookingStatusJPARepository bookingStatusRepository) {
        this.bookingRepository = bookingRepository;
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.bookingStatusRepository = bookingStatusRepository;
    }

    public List<Booking> getBookings(Long ownerId, Long sitterId, Long petId, String status, LocalDate bookingDate) {
        if (ownerId != null) {
            return bookingRepository.findByOwnerId(ownerId);
        }
        if (sitterId != null) {
            return bookingRepository.findBySitterId(sitterId);
        }
        if (petId != null) {
            return bookingRepository.findByPetId(petId);
        }
        if (status != null && !status.isBlank()) {
            BookingStatus bookingStatus = bookingStatusRepository.findByStatusName(status)
                .orElseThrow(() -> new IllegalArgumentException("Invalid status: " + status));
            return bookingRepository.findByStatus(bookingStatus);
        }
        if (bookingDate != null) {
            return bookingRepository.findByBookingDate(bookingDate);
        }
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId);
    }

    public List<Booking> getBookingsByOwner(Long ownerId) {
        return bookingRepository.findByOwnerId(ownerId);
    }

    public List<Booking> getBookingsBySitter(Long sitterId) {
        return bookingRepository.findBySitterId(sitterId);
    }

    public List<Booking> getBookingsByPet(Long petId) {
        return bookingRepository.findByPetId(petId);
    }

    public Booking createBooking(@NonNull LocalDate bookingDate, @NonNull LocalTime startTime,
                                @NonNull LocalTime endTime, @NonNull Long statusId, BigDecimal totalCost,
                                String specialRequests, @NonNull Long petId, @NonNull Long ownerId,
                                Long sitterId) {

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("Pet not found"));

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));

        User sitter = null;
        if (sitterId != null) {
            sitter = userRepository.findById(sitterId)
                    .orElseThrow(() -> new IllegalArgumentException("Sitter not found"));
        }

        BookingStatus status = bookingStatusRepository.findById(statusId)
                .orElseThrow(() -> new IllegalArgumentException("Booking status not found"));

        Booking booking = new Booking();
        booking.setBookingDate(bookingDate);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setStatus(status);
        booking.setPet(pet);
        booking.setOwner(owner);
        booking.setSitter(sitter);
        booking.setTotalCost(totalCost);
        booking.setSpecialRequests(specialRequests);

        return bookingRepository.save(booking);
    }

    public void updateBooking(@NonNull Long bookingId, @NonNull LocalDate bookingDate,
                              @NonNull LocalTime startTime, @NonNull LocalTime endTime,
                              @NonNull Long statusId, BigDecimal totalCost, String specialRequests,
                              @NonNull Long petId, @NonNull Long ownerId, @NonNull Long sitterId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("Pet not found"));

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));

        User sitter = userRepository.findById(sitterId)
                .orElseThrow(() -> new IllegalArgumentException("Sitter not found"));

        BookingStatus status = bookingStatusRepository.findById(statusId)
                .orElseThrow(() -> new IllegalArgumentException("Booking status not found"));

        booking.setBookingDate(bookingDate);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setStatus(status);
        booking.setTotalCost(totalCost);
        booking.setSpecialRequests(specialRequests);
        booking.setPet(pet);
        booking.setOwner(owner);
        booking.setSitter(sitter);

        bookingRepository.save(booking);
    }

    public void updateBookingStatus(@NonNull Long bookingId, @NonNull Long statusId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        BookingStatus status = bookingStatusRepository.findById(statusId)
                .orElseThrow(() -> new IllegalArgumentException("Booking status not found"));

        booking.setStatus(status);
        bookingRepository.save(booking);
    }

    public void deleteBooking(@NonNull Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        bookingRepository.delete(booking);
    }

    public List<Booking> getBookingsByDateRange(LocalDate startDate, LocalDate endDate) {
        return bookingRepository.findByBookingDateBetween(startDate, endDate);
    }

    public List<Booking> getBookingsByStatus(BookingStatus status) {
        return bookingRepository.findByStatus(status);
    }

    public boolean isTimeSlotAvailable(Long sitterId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<Booking> conflictingBookings = bookingRepository.findBySitterIdAndBookingDateAndTimeOverlap(
                sitterId, date, startTime, endTime);
        return conflictingBookings.isEmpty();
    }
}