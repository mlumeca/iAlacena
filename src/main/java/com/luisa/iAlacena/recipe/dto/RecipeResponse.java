package com.luisa.iAlacena.recipe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.luisa.iAlacena.recipe.model.Recipe;

import java.util.List;
import java.util.UUID;

public record RecipeResponse(
        Long id,
        String name,
        String description,
        int portions,
        //List<String> ingredients,
        //List<String> categories,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        UUID userId
) {
    public static RecipeResponse of(Recipe recipe) {
        return new RecipeResponse(
                recipe.getId(),
                recipe.getName(),
                recipe.getDescription(),
                recipe.getPortions(),
                //recipe.getIngredients(),
                //recipe.getCategories(),
                recipe.getUser().getId()
        );
    }
}