package com.luisa.iAlacena.ingredient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record EditIngredientRequest(
        @NotBlank(message = "name.required")
        @Size(min = 1, max = 100, message = "name.size")
        String name,

        List<Long> categoryIds
) {}