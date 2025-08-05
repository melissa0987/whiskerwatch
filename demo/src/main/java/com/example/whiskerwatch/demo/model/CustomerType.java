package com.example.whiskerwatch.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "customer_types")
@Getter
@Setter
@NoArgsConstructor
public class CustomerType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type_name", unique = true, nullable = false, length = 20)
    private String typeName; // 'OWNER', 'SITTER', 'BOTH'

    @OneToMany(mappedBy = "customerType")
    private Set<User> users;

    public CustomerType(String typeName) {
        this.typeName = typeName;
    }
}