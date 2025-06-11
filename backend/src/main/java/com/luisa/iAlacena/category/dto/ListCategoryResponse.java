package com.luisa.iAlacena.category.dto;

import com.luisa.iAlacena.category.model.Category;
import org.springframework.data.domain.Page;

import java.util.List;

public record ListCategoryResponse(
        long totalElements,
        int totalPages,
        int pageNumber,
        int pageSize,
        List<CategoryResponse> items
) {
    public static ListCategoryResponse of(Page<Category> categoryPage) {
        return new ListCategoryResponse(
                categoryPage.getTotalElements(),
                categoryPage.getTotalPages(),
                categoryPage.getNumber(),
                categoryPage.getSize(),
                categoryPage.getContent().stream()
                        .map(CategoryResponse::of)
                        .toList()
        );
    }
}