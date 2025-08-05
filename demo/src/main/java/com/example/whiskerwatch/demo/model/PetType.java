package com.example.whiskerwatch.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "pet_types")
@Getter
@Setter
@NoArgsConstructor
public class PetType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type_name", unique = true, nullable = false, length = 50)
    private String typeName;

    @OneToMany(mappedBy = "type")
    private Set<Pet> pets;

    public PetType(String typeName) {
        this.typeName = typeName;
    }
}
