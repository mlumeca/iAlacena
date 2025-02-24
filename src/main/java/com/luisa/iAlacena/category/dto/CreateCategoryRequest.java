package com.luisa.iAlacena.category.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(
        @NotBlank(message = "name.required")
        String name,

        Long parentCategoryId
) {}