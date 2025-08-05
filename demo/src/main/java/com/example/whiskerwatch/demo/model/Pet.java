package com.example.whiskerwatch.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "pets")
@Getter
@Setter
@NoArgsConstructor
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    private Integer age;

    @Column(length = 100)
    private String breed;

    @Column(precision = 5, scale = 2)
    private BigDecimal weight;

    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private PetType type;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL)
    private Set<Booking> bookings;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Pet(String name, Integer age, String breed, BigDecimal weight,
               String specialInstructions, User owner, PetType type) {
        this.name = name;
        this.age = age;
        this.breed = breed;
        this.weight = weight;
        this.specialInstructions = specialInstructions;
        this.owner = owner;
        this.type = type;
    }
}