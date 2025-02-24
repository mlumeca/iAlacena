package com.luisa.iAlacena.user.dto;

import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @NotBlank(message = "username.required")
        String username
) {}