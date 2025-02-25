package com.luisa.iAlacena.ingredient.dto;

import com.luisa.iAlacena.category.model.Category;
import com.luisa.iAlacena.ingredient.model.Ingredient;
import com.luisa.iAlacena.ingredient.model.UnitOfMeasure;

import java.util.List;

public record IngredientResponse(
        Long id,
        String name,
        int quantity,
        UnitOfMeasure unitOfMeasure,
        List<CategorySummary> categories
) {
    public static IngredientResponse of(Ingredient ingredient) {
        return new IngredientResponse(
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getQuantity(),
                ingredient.getUnitOfMeasure(),
                ingredient.getCategories().stream()
                        .map(CategorySummary::of)
                        .toList()
        );
    }

    public record CategorySummary(
            Long id,
            String name
    ) {
        public static CategorySummary of(Category category) {
            return new CategorySummary(category.getId(), category.getName());
        }
    }
}