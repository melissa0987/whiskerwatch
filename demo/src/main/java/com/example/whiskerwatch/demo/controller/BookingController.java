package com.example.whiskerwatch.demo.controller;

import com.example.whiskerwatch.demo.controller.request.BookingRequest;
import com.example.whiskerwatch.demo.controller.request.CreateGroup;
import com.example.whiskerwatch.demo.controller.request.UpdateGroup;
import com.example.whiskerwatch.demo.controller.response.BookingResponse;
import com.example.whiskerwatch.demo.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Validated
@RestController
@RequestMapping("/api/bookings")

public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getBookings(
            @RequestParam(name = "ownerId", required = false) Long ownerId,
            @RequestParam(name = "sitterId", required = false) Long sitterId,
            @RequestParam(name = "petId", required = false) Long petId,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "bookingDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bookingDate) {

        List<BookingResponse> bookings = bookingService.getBookings(ownerId, sitterId, petId, status, bookingDate)
                .stream()
                .map(BookingResponse::toResponse)
                .toList();

        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable Long bookingId) {
        Optional<BookingResponse> booking = bookingService.getBooking(bookingId)
                .map(BookingResponse::toResponse);

        return booking.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByOwner(@PathVariable Long ownerId) {
        List<BookingResponse> bookings = bookingService.getBookingsByOwner(ownerId)
                .stream()
                .map(BookingResponse::toResponse)
                .toList();

        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/sitter/{sitterId}")
    public ResponseEntity<List<BookingResponse>> getBookingsBySitter(@PathVariable Long sitterId) {
        List<BookingResponse> bookings = bookingService.getBookingsBySitter(sitterId)
                .stream()
                .map(BookingResponse::toResponse)
                .toList();

        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByPet(@PathVariable Long petId) {
        List<BookingResponse> bookings = bookingService.getBookingsByPet(petId)
                .stream()
                .map(BookingResponse::toResponse)
                .toList();

        return ResponseEntity.ok(bookings);
    }

    @PostMapping
    public ResponseEntity<Void> createBooking(@RequestBody @Validated(CreateGroup.class) BookingRequest bookingRequest) {
        try {
            bookingService.createBooking(
                    bookingRequest.getBookingDate(),
                    bookingRequest.getStartTime(),
                    bookingRequest.getEndTime(),
                    bookingRequest.getStatusId(),
                    bookingRequest.getTotalCost(),
                    bookingRequest.getSpecialRequests(),
                    bookingRequest.getPetId(),
                    bookingRequest.getOwnerId(),
                    bookingRequest.getSitterId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<Void> updateBooking(@PathVariable Long bookingId,
                                              @RequestBody @Validated(UpdateGroup.class) BookingRequest bookingRequest) {
        try {
            bookingService.updateBooking(
                    bookingId,
                    bookingRequest.getBookingDate(),
                    bookingRequest.getStartTime(),
                    bookingRequest.getEndTime(),
                    bookingRequest.getStatusId(),
                    bookingRequest.getTotalCost(),
                    bookingRequest.getSpecialRequests(),
                    bookingRequest.getPetId(),
                    bookingRequest.getOwnerId(),
                    bookingRequest.getSitterId()
            );
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{bookingId}/status")
    public ResponseEntity<Void> updateBookingStatus(@PathVariable Long bookingId,
                                                    @RequestParam Long statusId) {
        try {
            bookingService.updateBookingStatus(bookingId, statusId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long bookingId) {
        try {
            bookingService.deleteBooking(bookingId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Additional helpful endpoints
    @GetMapping("/upcoming")
    public ResponseEntity<List<BookingResponse>> getUpcomingBookings(
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(name = "userType", required = false) String userType) {

        LocalDate today = LocalDate.now();
        List<BookingResponse> bookings;

        if (userId != null && "sitter".equalsIgnoreCase(userType)) {
            bookings = bookingService.getBookingsBySitter(userId).stream()
                    .filter(booking -> booking.getBookingDate().isAfter(today) || booking.getBookingDate().isEqual(today))
                    .map(BookingResponse::toResponse)
                    .toList();
        } else if (userId != null && "owner".equalsIgnoreCase(userType)) {
            bookings = bookingService.getBookingsByOwner(userId).stream()
                    .filter(booking -> booking.getBookingDate().isAfter(today) || booking.getBookingDate().isEqual(today))
                    .map(BookingResponse::toResponse)
                    .toList();
        } else {
            bookings = bookingService.getBookings(null, null, null, null, null).stream()
                    .filter(booking -> booking.getBookingDate().isAfter(today) || booking.getBookingDate().isEqual(today))
                    .map(BookingResponse::toResponse)
                    .toList();
        }

        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/availability/{sitterId}")
    public ResponseEntity<Boolean> checkAvailability(
            @PathVariable Long sitterId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) String startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) String endTime) {

        try {
            boolean isAvailable = bookingService.isTimeSlotAvailable(
                    sitterId,
                    date,
                    java.time.LocalTime.parse(startTime),
                    java.time.LocalTime.parse(endTime)
            );
            return ResponseEntity.ok(isAvailable);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}