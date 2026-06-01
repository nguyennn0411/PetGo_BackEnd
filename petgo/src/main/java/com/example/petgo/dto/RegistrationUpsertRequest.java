package com.example.petgo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RegistrationUpsertRequest(
        @NotBlank(message = "Tên shop/doanh nghiệp không được để trống")
        @Size(max = 255, message = "Tên shop/doanh nghiệp tối đa 255 ký tự")
        String businessName,

        @NotBlank(message = "Số điện thoại shop không được để trống")
        @Size(max = 50, message = "Số điện thoại shop tối đa 50 ký tự")
        String businessPhone,

        @NotBlank(message = "Email shop không được để trống")
        @Email(message = "Email shop không hợp lệ")
        @Size(max = 255, message = "Email shop tối đa 255 ký tự")
        String businessEmail,

        @NotBlank(message = "Địa chỉ shop không được để trống")
        @Size(max = 500, message = "Địa chỉ shop tối đa 500 ký tự")
        String businessAddress,

        @Size(max = 100, message = "Mã số thuế tối đa 100 ký tự")
        String taxCode,

        @NotBlank(message = "Tên người đại diện không được để trống")
        @Size(max = 255, message = "Tên người đại diện tối đa 255 ký tự")
        String representativeName,

        @NotBlank(message = "Số điện thoại người đại diện không được để trống")
        @Size(max = 50, message = "Số điện thoại người đại diện tối đa 50 ký tự")
        String representativePhone,

        @NotBlank(message = "Email người đại diện không được để trống")
        @Email(message = "Email người đại diện không hợp lệ")
        @Size(max = 255, message = "Email người đại diện tối đa 255 ký tự")
        String representativeEmail,

        @Size(max = 2000, message = "Mô tả tối đa 2000 ký tự")
        String description,

        List<Long> serviceCategoryIds,

        List<String> locationImageUrls
) {
}