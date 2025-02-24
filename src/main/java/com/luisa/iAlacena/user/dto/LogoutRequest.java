package com.luisa.iAlacena.user.dto;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
        @NotBlank(message = "refreshToken.required")
        String refreshToken
) {}