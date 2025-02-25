package com.luisa.iAlacena.recipe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.luisa.iAlacena.category.model.Category;
import com.luisa.iAlacena.ingredient.model.Ingredient;
import com.luisa.iAlacena.recipe.model.Recipe;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RecipeResponse(
        Long id,
        String name,
        String description,
        int portions,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String imgUrl,
        List<IngredientSummary> ingredients,
        List<CategorySummary> categories,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        UUID userId
) {
    public static RecipeResponse of(Recipe recipe) {
        return new RecipeResponse(
                recipe.getId(),
                recipe.getName(),
                recipe.getDescription(),
                recipe.getPortions(),
                recipe.getCreatedAt(),
                recipe.getUpdatedAt(),
                recipe.getImgUrl(),
                recipe.getIngredients().stream()
                        .map(IngredientSummary::of)
                        .toList(),
                recipe.getCategories().stream()
                        .map(CategorySummary::of)
                        .toList(),
                recipe.getUser().getId()
        );
    }

    public record IngredientSummary(
            Long id,
            String name,
            int quantity,
            String unitOfMeasure
    ) {
        public static IngredientSummary of(Ingredient ingredient) {
            return new IngredientSummary(
                    ingredient.getId(),
                    ingredient.getName(),
                    ingredient.getQuantity(),
                    ingredient.getUnitOfMeasure().name()
            );
        }
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