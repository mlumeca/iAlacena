package com.luisa.iAlacena.favorites.repository;

import com.luisa.iAlacena.favorites.model.Favorites;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface FavoritesRepository extends JpaRepository<Favorites, Long> {

    @Query("""
            SELECT f
            FROM Favorites f
            WHERE f.user.id = :userId AND f.recipe.id = :recipeId
            """)
    Optional<Favorites> findByUserIdAndRecipeId(@Param("userId") UUID userId, @Param("recipeId") Long recipeId);

    void deleteByUserIdAndRecipeId(@Param("userId") UUID userId, @Param("recipeId") Long recipeId);

    @Query("""
            SELECT f
            FROM Favorites f
            LEFT JOIN FETCH f.recipe
            WHERE f.user.id = :userId
            """)
    Page<Favorites> findByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Favorites f WHERE f.recipe.id = :recipeId")
    void deleteByRecipeId(@Param("recipeId") Long recipeId);
}