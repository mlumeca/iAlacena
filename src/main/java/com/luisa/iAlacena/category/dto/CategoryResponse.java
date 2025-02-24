package com.luisa.iAlacena.category.dto;

import com.luisa.iAlacena.category.model.Category;

public record CategoryResponse(
        Long id,
        String name,
        Long parentCategoryId
) {
    public static CategoryResponse of(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getParentCategory() != null ? category.getParentCategory().getId() : null
        );
    }
}