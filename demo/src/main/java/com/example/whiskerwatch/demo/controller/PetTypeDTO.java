package com.example.whiskerwatch.demo.controller;

public class PetTypeDTO {
    private Long id;
    private String typeName;

    public PetTypeDTO(Long id, String typeName) {
        this.id = id;
        this.typeName = typeName;
    }

    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
}
