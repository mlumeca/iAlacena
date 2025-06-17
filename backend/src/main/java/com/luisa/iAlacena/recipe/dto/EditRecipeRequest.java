package com.luisa.iAlacena.recipe.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.List;

public record EditRecipeRequest(
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
        String description,

        @Min(value = 1, message = "Portions must be at least 1")
        Integer portions,

        String imgUrl, // Optional

        List<Long> categoryIds,

        List<Long> ingredientIds
) {}