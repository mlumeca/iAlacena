package com.luisa.iAlacena.user.dto;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank(message = "token.required")
        String token,

        @NotBlank(message = "password.required")
        String password
) {}