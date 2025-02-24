package com.luisa.iAlacena.category.dto;

import com.luisa.iAlacena.category.model.Category;

import java.util.List;

public record CategoryDetailResponse(
        Long id,
        String name,
        Long parentCategoryId,
        List<CategoryResponse> childCategories
) {
    public static CategoryDetailResponse of(Category category) {
        return new CategoryDetailResponse(
                category.getId(),
                category.getName(),
                category.getParentCategory() != null ? category.getParentCategory().getId() : null,
                category.getChildCategories().stream()
                        .map(CategoryResponse::of)
                        .toList()
        );
    }
}