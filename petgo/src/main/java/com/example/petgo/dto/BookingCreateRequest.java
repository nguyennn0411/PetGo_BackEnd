package com.example.petgo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingCreateRequest {

    @NotNull(message = "ID thú cưng không được để trống")
    Long petId;

    @NotNull(message = "ID khu vực không được để trống")
    Long areaId;

    @NotNull(message = "ID dịch vụ không được để trống")
    Long serviceId;

    @NotNull(message = "Ngày hẹn không được để trống")
    LocalDate appointmentDate;

    @NotNull(message = "Giờ bắt đầu không được để trống")
    LocalTime startTime;

    BigDecimal pickupLatitude;
    BigDecimal pickupLongitude;
    String pickupAddress;

    String promoCode;

    String customerNote;
}
