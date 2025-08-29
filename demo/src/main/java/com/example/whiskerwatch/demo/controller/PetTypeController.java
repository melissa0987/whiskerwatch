package com.example.whiskerwatch.demo.controller;

import java.util.List;
import java.util.stream.Collectors;
 
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController; 
import com.example.whiskerwatch.demo.repository.PetTypeJPARepository; 

@RestController
@RequestMapping("/api/pet-types")
@CrossOrigin(origins = "http://localhost:5173")
public class PetTypeController {

    private final PetTypeJPARepository petTypeRepository;

    public PetTypeController(PetTypeJPARepository petTypeRepository) {
        this.petTypeRepository = petTypeRepository;
    }

    @GetMapping
    public List<PetTypeDTO> getAllPetTypes() {
        return petTypeRepository.findAll()
                .stream()
                .map(petType -> new PetTypeDTO(petType.getId(), petType.getTypeName()))
                .collect(Collectors.toList());
    }
} 