package com.luisa.iAlacena.ingredient.dto;

import com.luisa.iAlacena.ingredient.model.Ingredient;
import org.springframework.data.domain.Page;

import java.util.List;

public record ListIngredientResponse(
        long totalElements,
        int totalPages,
        int pageNumber,
        int pageSize,
        List<IngredientResponse> items
) {
    public static ListIngredientResponse of(Page<Ingredient> ingredientPage) {
        return new ListIngredientResponse(
                ingredientPage.getTotalElements(),
                ingredientPage.getTotalPages(),
                ingredientPage.getNumber(),
                ingredientPage.getSize(),
                ingredientPage.getContent().stream()
                        .map(IngredientResponse::of)
                        .toList()
        );
    }
}