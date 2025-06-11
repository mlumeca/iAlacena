package com.luisa.iAlacena.recipe.dto;

import com.luisa.iAlacena.recipe.model.Recipe;
import org.springframework.data.domain.Page;

import java.util.List;

public record ListRecipeResponse(
        long totalElements,
        int totalPages,
        int pageNumber,
        int pageSize,
        List<RecipeResponse> items
) {
    public static ListRecipeResponse of(Page<Recipe> recipePage) {
        return new ListRecipeResponse(
                recipePage.getTotalElements(),
                recipePage.getTotalPages(),
                recipePage.getNumber(),
                recipePage.getSize(),
                recipePage.getContent().stream()
                        .map(RecipeResponse::of)
                        .toList()
        );
    }
}