package com.luisa.iAlacena.recipe.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;

public record EditRecipeRequest(
        @Size(min = 1, message = "name.min")
        String name,

        @Size(min = 1, message = "description.min")
        String description,

        @Min(value = 1, message = "portions.min")
        Integer portions,

        String imgUrl,

        List<Long> categoryIds,

        Map<Long, Integer> ingredients
) {}

