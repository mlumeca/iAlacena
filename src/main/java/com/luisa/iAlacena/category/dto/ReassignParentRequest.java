package com.luisa.iAlacena.category.dto;

import jakarta.validation.constraints.NotNull;

public record ReassignParentRequest(
        @NotNull(message = "parentId.required")
        Long parentId
) {}