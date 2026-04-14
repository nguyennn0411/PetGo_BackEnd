package com.example.petgo.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Long id;
    String fullName;
    String email;
    String phone;
    String avatarUrl;
    LocalDate dateOfBirth;
    String gender;
    String city;
    Boolean isActive;
    Set<String> roles;
}
