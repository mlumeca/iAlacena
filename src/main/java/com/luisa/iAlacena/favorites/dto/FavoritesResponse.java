package com.luisa.iAlacena.favorites.dto;

import com.luisa.iAlacena.favorites.model.Favorites;

import java.time.LocalDateTime;
import java.util.UUID;

public record FavoritesResponse(
        Long id,
        UUID userId,
        Long recipeId,
        LocalDateTime addedAt
) {
    public static FavoritesResponse of(Favorites favorite) {
        return new FavoritesResponse(
                favorite.getId(),
                favorite.getUser().getId(),
                favorite.getRecipe().getId(),
                favorite.getAddedAt()
        );
    }
}