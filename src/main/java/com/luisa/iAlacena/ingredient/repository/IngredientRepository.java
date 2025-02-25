package com.luisa.iAlacena.ingredient.repository;

import com.luisa.iAlacena.ingredient.model.Ingredient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("""
            SELECT i
            FROM Ingredient i
            LEFT JOIN FETCH i.categories
            """)
    Page<Ingredient> findAllWithCategories(Pageable pageable);

    @Query("""
            SELECT i
            FROM Ingredient i
            LEFT JOIN FETCH i.categories c
            WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))
            """)
    Page<Ingredient> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    @Query("""
            SELECT i
            FROM Ingredient i
            LEFT JOIN FETCH i.categories c
            WHERE c.id = :categoryId
            """)
    Page<Ingredient> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("""
            SELECT i
            FROM Ingredient i
            LEFT JOIN FETCH i.categories c
            WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))
            AND c.id = :categoryId
            """)
    Page<Ingredient> findByNameContainingIgnoreCaseAndCategoryId(
            @Param("name") String name,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );
}