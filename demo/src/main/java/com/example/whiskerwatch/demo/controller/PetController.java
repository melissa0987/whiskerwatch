package com.example.whiskerwatch.demo.controller;

import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RestController;

import com.example.whiskerwatch.demo.controller.request.CreateGroup;
import com.example.whiskerwatch.demo.controller.request.PetRequest;
import com.example.whiskerwatch.demo.controller.request.UpdateGroup;
import com.example.whiskerwatch.demo.controller.response.PetResponse;
import com.example.whiskerwatch.demo.model.Pet;
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
        return petService.getPet(petId)
                .map(PetResponse::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<PetResponse>> getPetsByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(petService.getPetsByOwner(ownerId)
                .stream()
                .map(PetResponse::toResponse)
                .toList());
    }

    @PostMapping
    public ResponseEntity<?> createPet(@RequestBody @Validated(CreateGroup.class) PetRequest petRequest) {
        try {
            Pet savedPet = petService.createPet(
                petRequest.getName(),
                petRequest.getAge(),
                petRequest.getBreed(),
                petRequest.getWeight(),
                petRequest.getSpecialInstructions(),
                petRequest.getOwnerId(),
                petRequest.getTypeId()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                        "success", true,
                        "message", "Pet created successfully",
                        "pet", PetResponse.toResponse(savedPet)
                    ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                        "success", false,
                        "message", e.getMessage()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "success", false,
                        "message", "Failed to create pet: " + e.getMessage()
                    ));
        }
    }

    @PutMapping("/{petId}")
    public ResponseEntity<?> updatePet(@PathVariable Long petId,
                          @RequestBody @Validated(UpdateGroup.class) PetRequest petRequest) {
        try {
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
            
            return ResponseEntity.ok()
                    .body(Map.of(
                        "success", true,
                        "message", "Pet updated successfully"
                    ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                        "success", false,
                        "message", e.getMessage()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "success", false,
                        "message", "Failed to update pet: " + e.getMessage()
                    ));
        }
    }

    @DeleteMapping("/{petId}")
    public ResponseEntity<?> deletePet(@PathVariable Long petId) {
        try {
            petService.deletePet(petId);
            return ResponseEntity.ok()
                    .body(Map.of(
                        "success", true,
                        "message", "Pet deleted successfully"
                    ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                        "success", false,
                        "message", e.getMessage()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "success", false,
                        "message", "Failed to delete pet: " + e.getMessage()
                    ));
        }
    }
}