package com.example.whiskerwatch.demo.controller;


import com.example.whiskerwatch.demo.controller.request.CreateGroup;
import com.example.whiskerwatch.demo.controller.request.PetRequest;
import com.example.whiskerwatch.demo.controller.request.UpdateGroup;
import com.example.whiskerwatch.demo.controller.response.PetResponse;
import com.example.whiskerwatch.demo.service.PetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/pets")
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