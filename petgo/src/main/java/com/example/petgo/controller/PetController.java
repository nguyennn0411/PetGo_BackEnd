package com.example.petgo.controller;

import com.example.petgo.dto.ApiMessageResponse;
import com.example.petgo.dto.PetListResponse;
import com.example.petgo.dto.PetResponse;
import com.example.petgo.dto.PetUpsertRequest;
import com.example.petgo.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/{ownerUserId}/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @GetMapping
    public ResponseEntity<PetListResponse> getOwnerPets(@PathVariable Long ownerUserId) {
        return ResponseEntity.ok(petService.getPetsByOwner(ownerUserId));
    }

    @GetMapping("/{petId}")
    public ResponseEntity<PetResponse> getPetDetail(
            @PathVariable Long ownerUserId,
            @PathVariable Long petId
    ) {
        return ResponseEntity.ok(petService.getPetDetail(ownerUserId, petId));
    }

    @PostMapping
    public ResponseEntity<PetResponse> createPet(
            @PathVariable Long ownerUserId,
            @Valid @RequestBody PetUpsertRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(petService.createPet(ownerUserId, request));
    }

    @PutMapping("/{petId}")
    public ResponseEntity<PetResponse> updatePet(
            @PathVariable Long ownerUserId,
            @PathVariable Long petId,
            @Valid @RequestBody PetUpsertRequest request
    ) {
        return ResponseEntity.ok(petService.updatePet(ownerUserId, petId, request));
    }

    @DeleteMapping("/{petId}")
    public ResponseEntity<ApiMessageResponse> deletePet(
            @PathVariable Long ownerUserId,
            @PathVariable Long petId
    ) {
        petService.deletePet(ownerUserId, petId);
        return ResponseEntity.ok(ApiMessageResponse.builder().message("Đã xóa thú cưng thành công").build());
    }
}
