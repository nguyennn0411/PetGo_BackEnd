package com.example.petgo.controller;

import com.example.petgo.dto.ApiMessageResponse;
import com.example.petgo.dto.PetListResponse;
import com.example.petgo.dto.PetResponse;
import com.example.petgo.dto.PetUpsertRequest;
import com.example.petgo.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PetResponse> createPet(
            @PathVariable Long ownerUserId,
            @Valid @RequestPart("data") PetUpsertRequest request,
            @RequestPart(value = "avatarFile", required = false) MultipartFile avatarFile
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(petService.createPet(ownerUserId, request, avatarFile));
    }

    @PutMapping(value = "/{petId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PetResponse> updatePet(
            @PathVariable Long ownerUserId,
            @PathVariable Long petId,
            @Valid @RequestPart("data") PetUpsertRequest request,
            @RequestPart(value = "avatarFile", required = false) MultipartFile avatarFile
    ) {
        return ResponseEntity.ok(petService.updatePet(ownerUserId, petId, request, avatarFile));
    }

    @DeleteMapping("/{petId}")
    public ResponseEntity<ApiMessageResponse> deletePet(
            @PathVariable Long ownerUserId,
            @PathVariable Long petId
    ) {
        petService.deletePet(ownerUserId, petId);

        return ResponseEntity.ok(
                ApiMessageResponse.builder()
                        .message("Đã xóa thú cưng thành công")
                        .build()
        );
    }
}