package com.example.petgo.service;

import com.example.petgo.dto.PetListResponse;
import com.example.petgo.dto.PetResponse;
import com.example.petgo.dto.PetUpsertRequest;

public interface PetService {

    PetListResponse getPetsByOwner(Long ownerUserId);

    PetResponse getPetDetail(Long ownerUserId, Long petId);

    PetResponse createPet(Long ownerUserId, PetUpsertRequest request);

    PetResponse updatePet(Long ownerUserId, Long petId, PetUpsertRequest request);

    void deletePet(Long ownerUserId, Long petId);
}
