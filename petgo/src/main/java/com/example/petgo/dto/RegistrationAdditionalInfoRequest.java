package com.example.petgo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationAdditionalInfoRequest(
                @NotBlank(message = "Thông tin bổ sung không được để trống") @Size(max = 4000, message = "Thông tin bổ sung tối đa 4000 ký tự") String additionalInformation,

                @Valid RegistrationUpsertRequest application) {
}