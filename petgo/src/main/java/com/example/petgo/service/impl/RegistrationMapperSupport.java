package com.example.petgo.service.impl;

import com.example.petgo.dto.RegistrationResponse;
import com.example.petgo.dto.RegistrationServiceCategoryResponse;
import com.example.petgo.entity.RegistrationApplication;
import com.example.petgo.entity.ServiceCategory;
import com.example.petgo.repository.ServiceCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class RegistrationMapperSupport {

    private final ServiceCategoryRepository serviceCategoryRepository;

    public RegistrationResponse toResponse(RegistrationApplication application) {
        if (application == null) {
            return null;
        }

        List<Long> categoryIds = parseLongCsv(application.getServiceCategoryIds());
        List<RegistrationServiceCategoryResponse> categories = categoryIds.isEmpty()
                ? List.of()
                : serviceCategoryRepository.findAllById(categoryIds).stream()
                        .map(this::toCategoryResponse)
                        .toList();

        return RegistrationResponse.builder()
                .id(application.getId())
                .type(application.getType())
                .status(application.getStatus())
                .userId(application.getUser() != null ? application.getUser().getId() : null)
                .userName(application.getUser() != null ? application.getUser().getFullName() : null)
                .userEmail(application.getUser() != null ? application.getUser().getEmail() : null)
                .userPhone(application.getUser() != null ? application.getUser().getPhoneNumber() : null)
                .businessName(application.getBusinessName())
                .businessPhone(application.getBusinessPhone())
                .businessEmail(application.getBusinessEmail())
                .businessAddress(application.getBusinessAddress())
                .taxCode(application.getTaxCode())
                .representativeName(application.getRepresentativeName())
                .representativePhone(application.getRepresentativePhone())
                .representativeEmail(application.getRepresentativeEmail())
                .description(application.getDescription())
                .serviceCategoryIds(categoryIds)
                .serviceCategories(categories)
                .locationImageUrls(parseTextLines(application.getLocationImageUrls()))
                .additionalInformation(application.getAdditionalInformation())
                .adminMessage(application.getAdminMessage())
                .rejectionReason(application.getRejectionReason())
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .submittedAt(application.getSubmittedAt())
                .reviewedAt(application.getReviewedAt())
                .reviewerId(application.getReviewer() != null ? application.getReviewer().getId() : null)
                .reviewerName(application.getReviewer() != null ? application.getReviewer().getFullName() : null)
                .build();
    }

    public RegistrationServiceCategoryResponse toCategoryResponse(ServiceCategory category) {
        return RegistrationServiceCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .build();
    }

    public String toCsv(List<Long> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.stream()
                .filter(Objects::nonNull)
                .distinct()
                .map(String::valueOf)
                .reduce((left, right) -> left + "," + right)
                .orElse(null);
    }

    public String toTextLines(List<String> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .distinct()
                .reduce((left, right) -> left + "\n" + right)
                .orElse(null);
    }

    public List<Long> parseLongCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            return List.of();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(value -> {
                    try {
                        return Long.parseLong(value);
                    } catch (NumberFormatException ignored) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    public List<String> parseTextLines(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return Arrays.stream(text.split("\\R"))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList();
    }

    public List<RegistrationResponse> toHistoryList(RegistrationApplication application) {
        return application == null ? Collections.emptyList() : List.of(toResponse(application));
    }
}