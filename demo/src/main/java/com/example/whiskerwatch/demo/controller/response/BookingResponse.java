package com.example.whiskerwatch.demo.controller.response;

import com.example.whiskerwatch.demo.model.Booking;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class BookingResponse {
    private Long bookingId;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String statusName;
    private BigDecimal totalCost;
    private String specialRequests;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Pet information
    private Long petId;
    private String petName;
    private String petTypeName;

    // Owner information
    private Long ownerId;
    private String ownerName;
    private String ownerEmail;

    // Sitter information
    private Long sitterId;
    private String sitterName;
    private String sitterEmail;

    public static BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getBookingDate(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getStatus() != null ? booking.getStatus().getStatusName() : null,
                booking.getTotalCost(),
                booking.getSpecialRequests(),
                booking.getCreatedAt(),
                booking.getUpdatedAt(),

                // Pet information
                booking.getPet() != null ? booking.getPet().getId() : null,
                booking.getPet() != null ? booking.getPet().getName() : null,
                booking.getPet() != null && booking.getPet().getType() != null ? booking.getPet().getType().getTypeName() : null,

                // Owner information
                booking.getOwner() != null ? booking.getOwner().getId() : null,
                booking.getOwner() != null ? booking.getOwner().getFirstName() + " " + booking.getOwner().getLastName() : null,
                booking.getOwner() != null ? booking.getOwner().getEmail() : null,

                // Sitter information
                booking.getSitter() != null ? booking.getSitter().getId() : null,
                booking.getSitter() != null ? booking.getSitter().getFirstName() + " " + booking.getSitter().getLastName() : null,
                booking.getSitter() != null ? booking.getSitter().getEmail() : null
        );
    }
}