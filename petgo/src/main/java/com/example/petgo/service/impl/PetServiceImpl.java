package com.example.petgo.service.impl;

import com.example.petgo.dto.PetListResponse;
import com.example.petgo.dto.PetPhotoResponse;
import com.example.petgo.dto.PetResponse;
import com.example.petgo.dto.PetUpsertRequest;
import com.example.petgo.entity.Pet;
import com.example.petgo.entity.PetPhoto;
import com.example.petgo.entity.User;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.PetPhotoRepository;
import com.example.petgo.repository.PetRepository;
import com.example.petgo.repository.UserRepository;
import com.example.petgo.service.CloudinaryStorageService;
import com.example.petgo.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final PetPhotoRepository petPhotoRepository;
    private final UserRepository userRepository;
    private final CloudinaryStorageService cloudinaryStorageService;

    @Value("${app.pets.max-photo-limit:5}")
    private int maxPhotoLimit;

    @Override
    public PetListResponse getPetsByOwner(Long ownerUserId) {
        ensureOwnerExists(ownerUserId);

        List<Pet> pets = petRepository.findActiveByOwnerUserId(ownerUserId);

        return PetListResponse.builder()
                .ownerUserId(ownerUserId)
                .total(pets.size())
                .items(pets.stream().map(this::mapPet).toList())
                .build();
    }

    @Override
    public PetResponse getPetDetail(Long ownerUserId, Long petId) {
        return mapPet(getOwnedPet(ownerUserId, petId));
    }

    @Override
    @Transactional
    public PetResponse createPet(
            Long ownerUserId,
            PetUpsertRequest request,
            MultipartFile avatarFile
    ) {
        User owner = ensureOwnerExists(ownerUserId);
        validateRequest(request);

        String uploadedAvatarUrl = cloudinaryStorageService.uploadPetAvatar(avatarFile);

        Pet pet = new Pet();
        pet.setPetCode(generatePetCode());
        pet.setOwner(owner);
        pet.setStatus("ACTIVE");

        applyRequest(pet, request, uploadedAvatarUrl);

        pet = petRepository.save(pet);

        savePhotos(pet, request.photoUrls(), pet.getAvatarUrl());

        return mapPet(pet);
    }

    @Override
    @Transactional
    public PetResponse updatePet(
            Long ownerUserId,
            Long petId,
            PetUpsertRequest request,
            MultipartFile avatarFile
    ) {
        validateRequest(request);

        Pet pet = getOwnedPet(ownerUserId, petId);

        String uploadedAvatarUrl = cloudinaryStorageService.uploadPetAvatar(avatarFile);

        applyRequest(pet, request, uploadedAvatarUrl);

        pet = petRepository.save(pet);

        petPhotoRepository.deleteByPet_Id(pet.getId());
        savePhotos(pet, request.photoUrls(), pet.getAvatarUrl());

        return mapPet(pet);
    }

    @Override
    @Transactional
    public void deletePet(Long ownerUserId, Long petId) {
        Pet pet = getOwnedPet(ownerUserId, petId);

        pet.setStatus("INACTIVE");
        pet.setDeletedAt(LocalDateTime.now());

        petRepository.save(pet);
    }

    private User ensureOwnerExists(Long ownerUserId) {
        if (ownerUserId == null) {
            throw new BadRequestException("Thiếu ownerUserId");
        }

        return userRepository.findById(ownerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng sở hữu thú cưng"));
    }

    private Pet getOwnedPet(Long ownerUserId, Long petId) {
        ensureOwnerExists(ownerUserId);

        return petRepository.findOwnedActivePet(ownerUserId, petId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thú cưng"));
    }

    private void validateRequest(PetUpsertRequest request) {
        if (request == null) {
            throw new BadRequestException("Thiếu dữ liệu thú cưng");
        }

        int photoCount = request.photoUrls() == null ? 0 : request.photoUrls().size();

        if (photoCount > maxPhotoLimit) {
            throw new BadRequestException("Số lượng ảnh tối đa là " + maxPhotoLimit);
        }

        if (request.weightKg() != null && request.weightKg().signum() < 0) {
            throw new BadRequestException("Cân nặng không hợp lệ");
        }
    }

    private void applyRequest(
            Pet pet,
            PetUpsertRequest request,
            String uploadedAvatarUrl
    ) {
        pet.setName(trimToNull(request.name()));
        pet.setSpecies(normalizeUpper(request.species(), "OTHER"));
        pet.setBreed(trimToNull(request.breed()));
        pet.setGender(normalizeUpper(request.gender(), "UNKNOWN"));
        pet.setDateOfBirth(request.dateOfBirth());
        pet.setAgeLabel(trimToNull(request.ageLabel()));
        pet.setWeightKg(request.weightKg());
        pet.setColor(trimToNull(request.color()));
        pet.setSize(normalizeUpper(request.size(), "UNKNOWN"));

        String finalAvatarUrl = firstNonBlank(
                uploadedAvatarUrl,
                request.avatarUrl(),
                pet.getAvatarUrl()
        );
        pet.setAvatarUrl(trimToNull(finalAvatarUrl));

        pet.setHealthNotes(trimToNull(request.healthNotes()));
        pet.setAllergyNotes(trimToNull(request.allergyNotes()));
        pet.setBehaviorNotes(trimToNull(request.behaviorNotes()));
        pet.setVaccinationNotes(trimToNull(request.vaccinationNotes()));
    }

    private void savePhotos(Pet pet, List<String> photoUrls, String avatarUrl) {
        List<String> normalized = new ArrayList<>();

        if (avatarUrl != null && !avatarUrl.isBlank()) {
            normalized.add(avatarUrl.trim());
        }

        if (photoUrls != null) {
            for (String photoUrl : photoUrls) {
                if (
                        photoUrl != null
                                && !photoUrl.isBlank()
                                && normalized.stream().noneMatch(existing -> existing.equals(photoUrl.trim()))
                ) {
                    normalized.add(photoUrl.trim());
                }
            }
        }

        for (int i = 0; i < normalized.size(); i++) {
            PetPhoto photo = new PetPhoto();

            photo.setPet(pet);
            photo.setPhotoUrl(normalized.get(i));
            photo.setPrimary(i == 0);
            photo.setSortOrder(i);

            petPhotoRepository.save(photo);
        }
    }

    private PetResponse mapPet(Pet pet) {
        List<PetPhotoResponse> photos = petPhotoRepository
                .findByPet_IdOrderByPrimaryDescSortOrderAscIdAsc(pet.getId())
                .stream()
                .map(photo -> PetPhotoResponse.builder()
                        .id(photo.getId())
                        .photoUrl(photo.getPhotoUrl())
                        .primary(Boolean.TRUE.equals(photo.getPrimary()))
                        .sortOrder(photo.getSortOrder())
                        .build()
                )
                .toList();

        String avatar = firstNonBlank(
                pet.getAvatarUrl(),
                photos.stream().map(PetPhotoResponse::photoUrl).findFirst().orElse(null),
                fallbackPetAvatar(pet)
        );

        return PetResponse.builder()
                .id(pet.getId())
                .petCode(pet.getPetCode())
                .ownerUserId(pet.getOwner() != null ? pet.getOwner().getId() : null)
                .name(pet.getName())
                .species(pet.getSpecies())
                .speciesLabel(mapSpeciesLabel(pet.getSpecies()))
                .breed(pet.getBreed())
                .gender(pet.getGender())
                .genderLabel(mapGenderLabel(pet.getGender()))
                .dateOfBirth(pet.getDateOfBirth())
                .ageLabel(pet.getAgeLabel())
                .weightKg(pet.getWeightKg())
                .color(pet.getColor())
                .size(pet.getSize())
                .sizeLabel(mapSizeLabel(pet.getSize()))
                .avatarUrl(avatar)
                .healthNotes(pet.getHealthNotes())
                .allergyNotes(pet.getAllergyNotes())
                .behaviorNotes(pet.getBehaviorNotes())
                .vaccinationNotes(pet.getVaccinationNotes())
                .status(pet.getStatus())
                .photos(photos)
                .createdAt(pet.getCreatedAt())
                .updatedAt(pet.getUpdatedAt())
                .build();
    }

    private String generatePetCode() {
        return "PET-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10)
                .toUpperCase(Locale.ROOT);
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String normalizeUpper(String value, String fallback) {
        String normalized = trimToNull(value);
        return normalized == null ? fallback : normalized.toUpperCase(Locale.ROOT);
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }

        return null;
    }

    private String fallbackPetAvatar(Pet pet) {
        String seed = firstNonBlank(
                pet.getName(),
                pet.getPetCode(),
                String.valueOf(pet.getId())
        );

        return "https://images.unsplash.com/photo-1517849845537-4d257902454a?auto=format&fit=crop&q=80&w=800&sig="
                + Math.abs(Objects.hashCode(seed));
    }

    private String mapSpeciesLabel(String species) {
        if (species == null) return "Khác";

        return switch (species.toUpperCase(Locale.ROOT)) {
            case "DOG" -> "Chó";
            case "CAT" -> "Mèo";
            case "BIRD" -> "Chim";
            case "RABBIT" -> "Thỏ";
            case "HAMSTER" -> "Hamster";
            case "REPTILE" -> "Bò sát";
            default -> "Khác";
        };
    }

    private String mapGenderLabel(String gender) {
        if (gender == null) return "Chưa rõ";

        return switch (gender.toUpperCase(Locale.ROOT)) {
            case "MALE" -> "Đực";
            case "FEMALE" -> "Cái";
            default -> "Chưa rõ";
        };
    }

    private String mapSizeLabel(String size) {
        if (size == null) return "Chưa rõ";

        return switch (size.toUpperCase(Locale.ROOT)) {
            case "XS" -> "Rất nhỏ";
            case "S" -> "Nhỏ";
            case "M" -> "Vừa";
            case "L" -> "Lớn";
            case "XL" -> "Rất lớn";
            default -> "Chưa rõ";
        };
    }
}