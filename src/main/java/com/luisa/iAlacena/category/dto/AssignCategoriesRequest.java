package com.luisa.iAlacena.category.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AssignCategoriesRequest(
        @NotEmpty(message = "categories.required")
        List<Long> categoryIds
) {}