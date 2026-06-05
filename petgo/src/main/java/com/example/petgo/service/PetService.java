package com.example.petgo.service;

import com.example.petgo.dto.PetListResponse;
import com.example.petgo.dto.PetResponse;
import com.example.petgo.dto.PetUpsertRequest;
import org.springframework.web.multipart.MultipartFile;

public interface PetService {

    PetListResponse getPetsByOwner(Long ownerUserId);

    PetResponse getPetDetail(Long ownerUserId, Long petId);

    PetResponse createPet(
            Long ownerUserId,
            PetUpsertRequest request,
            MultipartFile avatarFile
    );

    PetResponse updatePet(
            Long ownerUserId,
            Long petId,
            PetUpsertRequest request,
            MultipartFile avatarFile
    );

    void deletePet(Long ownerUserId, Long petId);
}