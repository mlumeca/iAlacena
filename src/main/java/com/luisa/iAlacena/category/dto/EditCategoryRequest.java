package com.luisa.iAlacena.category.dto;

import jakarta.validation.constraints.NotBlank;

public record EditCategoryRequest(
        @NotBlank(message = "name.required")
        String name,

        Long parentCategoryId // Puede ser null para no cambiar o eliminar la relación
) {}