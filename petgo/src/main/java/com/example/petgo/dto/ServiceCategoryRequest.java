package com.example.petgo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceCategoryRequest {
    
    @NotBlank(message = "Tên danh mục không được để trống")
    String name;

    String slug;

    String description;

    String iconKey;

    Integer sortOrder;

    @Builder.Default
    Boolean active = true;
}
