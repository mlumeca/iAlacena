package com.luisa.iAlacena.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EditUserRequest(
        @NotBlank(message = "username.required")
        @Size(min = 3, max = 20, message = "username.size")
        String username,

        @NotBlank(message = "email.required")
        @Email(message = "email.invalid")
        String email
) {}