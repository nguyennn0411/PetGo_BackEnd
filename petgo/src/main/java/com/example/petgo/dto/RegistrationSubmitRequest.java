package com.example.petgo.dto;

import jakarta.validation.Valid;

public record RegistrationSubmitRequest(
                @Valid RegistrationUpsertRequest application) {
}