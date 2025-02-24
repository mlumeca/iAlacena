package com.luisa.iAlacena.ingredient.dto;

import com.luisa.iAlacena.ingredient.model.UnitOfMeasure;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateIngredientRequest(
        @NotBlank(message = "name.required")
        String name,

        @NotNull(message = "unitOfMeasure.required")
        UnitOfMeasure unitOfMeasure
) {}