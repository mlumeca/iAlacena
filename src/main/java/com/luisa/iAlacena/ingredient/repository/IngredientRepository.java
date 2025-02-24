package com.luisa.iAlacena.ingredient.repository;

import com.luisa.iAlacena.ingredient.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    @Query("""
            SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END
            FROM Ingredient i
            WHERE i.name = :name
            """)
    boolean existsByName(@Param("name") String name);

    @Query("""
            SELECT i
            FROM Ingredient i
            WHERE i.name = :name
            """)
    Optional<Ingredient> findByName(@Param("name") String name);
}