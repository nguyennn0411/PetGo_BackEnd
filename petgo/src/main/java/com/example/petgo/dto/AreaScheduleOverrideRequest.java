package com.example.petgo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AreaScheduleOverrideRequest {

    @NotNull(message = "Ngày override không được để trống")
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate overrideDate;

    LocalTime openTime;
    LocalTime closeTime;

    @Builder.Default
    Boolean closed = false;

    String reason;
}
