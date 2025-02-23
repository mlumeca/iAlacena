package com.luisa.iAlacena.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "username.required")
        String username,

        @NotBlank(message = "password.required")
        String password
) {}