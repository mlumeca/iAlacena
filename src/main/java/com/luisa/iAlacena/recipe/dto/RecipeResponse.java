package com.luisa.iAlacena.recipe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.luisa.iAlacena.category.model.Category;
import com.luisa.iAlacena.recipe.model.Recipe;

import java.util.List;
import java.util.UUID;

public record RecipeResponse(
        Long id,
        String name,
        String description,
        int portions,
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
                recipe.getCategories().stream()
                        .map(CategorySummary::of)
                        .toList(),
                recipe.getUser().getId()
        );
    }

    // Nested record for category summary
    public record CategorySummary(
            Long id,
            String name
    ) {
        public static CategorySummary of(Category category) {
            return new CategorySummary(category.getId(), category.getName());
        }
    }
}