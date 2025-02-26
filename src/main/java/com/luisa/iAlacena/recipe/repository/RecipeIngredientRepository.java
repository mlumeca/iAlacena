package com.luisa.iAlacena.recipe.repository;

import com.luisa.iAlacena.recipe.model.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {

    @Modifying
    @Query("DELETE FROM RecipeIngredient ri WHERE ri.ingredient.id = :ingredientId")
    void deleteByIngredientId(@Param("ingredientId") Long ingredientId);
}