package com.luisa.iAlacena.recipe.repository;

import com.luisa.iAlacena.recipe.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Query("""
            SELECT r
            FROM Recipe r
            LEFT JOIN FETCH r.user
            """)
    List<Recipe> findAllWithUser();
}