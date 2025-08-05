package com.example.whiskerwatch.demo.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingRequest {

    @NotNull(groups = {CreateGroup.class, UpdateGroup.class})
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate bookingDate;

    @NotNull(groups = {CreateGroup.class, UpdateGroup.class})
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime startTime;

    @NotNull(groups = {CreateGroup.class, UpdateGroup.class})
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime endTime;

    @NotNull(groups = {CreateGroup.class, UpdateGroup.class})
    private Long statusId;

    private BigDecimal totalCost;

    private String specialRequests;

    @NotNull(groups = {CreateGroup.class, UpdateGroup.class})
    private Long petId;

    @NotNull(groups = {CreateGroup.class, UpdateGroup.class})
    private Long ownerId;

    @NotNull(groups = {CreateGroup.class, UpdateGroup.class})
    private Long sitterId;
}