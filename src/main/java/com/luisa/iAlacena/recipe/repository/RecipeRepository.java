package com.luisa.iAlacena.recipe.repository;

import com.luisa.iAlacena.recipe.model.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Query("""
            SELECT r
            FROM Recipe r
            LEFT JOIN FETCH r.user
            """)
    List<Recipe> findAllWithUser();

    @Query("""
            SELECT r
            FROM Recipe r
            LEFT JOIN FETCH r.user
            LEFT JOIN FETCH r.categories
            """)
    Page<Recipe> findAllWithUserAndCategories(Pageable pageable);

    @Query("""
            SELECT r
            FROM Recipe r
            LEFT JOIN FETCH r.user
            LEFT JOIN FETCH r.categories c
            WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))
            """)
    Page<Recipe> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    @Query("""
            SELECT r
            FROM Recipe r
            LEFT JOIN FETCH r.user
            LEFT JOIN FETCH r.categories c
            WHERE c.id = :categoryId
            """)
    Page<Recipe> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("""
            SELECT r
            FROM Recipe r
            LEFT JOIN FETCH r.user
            LEFT JOIN FETCH r.categories c
            WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))
            AND c.id = :categoryId
            """)
    Page<Recipe> findByNameContainingIgnoreCaseAndCategoryId(
            @Param("name") String name,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );

    @Query("""
            SELECT r
            FROM Recipe r
            LEFT JOIN FETCH r.user
            LEFT JOIN FETCH r.categories
            LEFT JOIN FETCH r.ingredients
            WHERE r.id = :id
            """)
    Optional<Recipe> findByIdWithDetails(@Param("id") Long id);
}