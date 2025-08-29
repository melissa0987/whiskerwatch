package com.example.whiskerwatch.demo.controller;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.whiskerwatch.demo.controller.request.CreateGroup;
import com.example.whiskerwatch.demo.controller.request.PetRequest;
import com.example.whiskerwatch.demo.controller.request.UpdateGroup;
import com.example.whiskerwatch.demo.controller.response.PetResponse;
import com.example.whiskerwatch.demo.service.PetService;

@Validated
@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "http://localhost:5173")
public class PetController {
    private final PetService petService;
    

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    public ResponseEntity<List<PetResponse>> getPets(
            @RequestParam(name = "ownerId", required = false) Long ownerId,
            @RequestParam(name = "petType", required = false) String petType,
            @RequestParam(name = "isActive", required = false) Boolean isActive) {
        return ResponseEntity.ok(petService.getPets(ownerId, petType, isActive)
                .stream()
                .map(PetResponse::toResponse)
                .toList());
    }

    @GetMapping("/{petId}")
    public ResponseEntity<PetResponse> getPet(@PathVariable Long petId) {
        return ResponseEntity.ok(petService.getPet(petId)
                .map(PetResponse::toResponse)
                .orElse(null));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<PetResponse>> getPetsByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(petService.getPetsByOwner(ownerId)
                .stream()
                .map(PetResponse::toResponse)
                .toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createPet(@RequestBody @Validated(CreateGroup.class) PetRequest petRequest) {
        petService.createPet(
                petRequest.getName(),
                petRequest.getAge(),
                petRequest.getBreed(),
                petRequest.getWeight(),
                petRequest.getSpecialInstructions(),
                petRequest.getOwnerId(),
                petRequest.getTypeId()
        );
    }

    @PutMapping("/{petId}")
    @ResponseStatus(HttpStatus.OK)
    public void updatePet(@PathVariable Long petId,
                          @RequestBody @Validated(UpdateGroup.class) PetRequest petRequest) {
        petService.updatePet(
                petId,
                petRequest.getName(),
                petRequest.getAge(),
                petRequest.getBreed(),
                petRequest.getWeight(),
                petRequest.getSpecialInstructions(),
                petRequest.getOwnerId(),
                petRequest.getTypeId(),
                petRequest.getIsActive()
        );
    }

    @DeleteMapping("/{petId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePet(@PathVariable Long petId) {
        petService.deletePet(petId);
    }
}