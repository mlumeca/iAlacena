package com.luisa.iAlacena.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "oldPassword.required")
        String oldPassword,

        @NotBlank(message = "newPassword.required")
        @Size(min = 8, message = "newPassword.size")
        String newPassword
) {}