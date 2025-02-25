package com.luisa.iAlacena.category.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSubcategoryRequest(
        @NotBlank(message = "name.required")
        String name
) {}