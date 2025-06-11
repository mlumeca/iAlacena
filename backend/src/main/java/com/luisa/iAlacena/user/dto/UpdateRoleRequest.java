package com.luisa.iAlacena.user.dto;

import com.luisa.iAlacena.user.model.UserRole;
import jakarta.validation.constraints.NotNull;

public record UpdateRoleRequest(
        @NotNull(message = "role.required")
        UserRole role
) {}