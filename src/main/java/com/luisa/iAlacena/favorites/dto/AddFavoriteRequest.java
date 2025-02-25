package com.luisa.iAlacena.favorites.dto;

import jakarta.validation.constraints.NotNull;

public record AddFavoriteRequest(
        @NotNull(message = "recipeId.required")
        Long recipeId
) {}