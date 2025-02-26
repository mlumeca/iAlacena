package com.luisa.iAlacena.recipe.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateRecipeRequest(
        @NotBlank(message = "name.required")
        String name,

        @NotBlank(message = "description.required")
        String description,

        @Min(value = 1, message = "portions.min")
        int portions,

        List<Long> categoryIds
) {}