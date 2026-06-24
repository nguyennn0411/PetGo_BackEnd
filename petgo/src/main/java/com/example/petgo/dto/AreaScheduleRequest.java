package com.example.petgo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AreaScheduleRequest {

    @NotNull(message = "Thứ trong tuần không được để trống")
    Integer dayOfWeek;

    @NotNull(message = "Giờ mở cửa không được để trống")
    LocalTime openTime;

    @NotNull(message = "Giờ đóng cửa không được để trống")
    LocalTime closeTime;

    @Builder.Default
    Boolean active = true;
}
