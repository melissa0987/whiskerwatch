package com.example.whiskerwatch.demo.controller;

public class PetTypeDTO {
    private Long id;
    private String typeName;

    public PetTypeDTO(Long id, String typeName) {
        this.id = id;
        this.typeName = typeName;
    }

    // Getters and setters (or use Lombok @Data for simplicity)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
}
