package com.luisa.iAlacena.ingredient.dto;

import com.luisa.iAlacena.ingredient.model.Ingredient;
import com.luisa.iAlacena.ingredient.model.UnitOfMeasure;

public record IngredientResponse(
        Long id,
        String name,
        int quantity,
        UnitOfMeasure unitOfMeasure
) {
    public static IngredientResponse of(Ingredient ingredient) {
        return new IngredientResponse(
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getQuantity(),
                ingredient.getUnitOfMeasure()
        );
    }
}