package com.example.whiskerwatch.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "booking_statuses")
@Getter
@Setter
@NoArgsConstructor
public class BookingStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status_name", unique = true, nullable = false, length = 20)
    private String statusName; // 'PENDING', 'CONFIRMED', etc.

    @OneToMany(mappedBy = "status")
    private Set<Booking> bookings;

    public BookingStatus(String statusName) {
        this.statusName = statusName;
    }
}