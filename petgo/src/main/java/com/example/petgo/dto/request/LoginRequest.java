package com.example.petgo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {

    @NotBlank(message = "USERNAME_REQUIRED")
    String userName;

    @NotBlank(message = "PASSWORD_REQUIRED")
    String password;

}
