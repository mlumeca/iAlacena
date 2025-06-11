package com.luisa.iAlacena.inventory.repository;

import com.luisa.iAlacena.inventory.model.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query("""
            SELECT i
            FROM Inventory i
            LEFT JOIN FETCH i.ingredient
            WHERE i.user.id = :userId
            """)
    Page<Inventory> findByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("""
            SELECT i
            FROM Inventory i
            WHERE i.user.id = :userId AND i.ingredient.id = :ingredientId
            """)
    Optional<Inventory> findByUserIdAndIngredientId(@Param("userId") UUID userId, @Param("ingredientId") Long ingredientId);

    @Modifying
    @Query("DELETE FROM Inventory i WHERE i.ingredient.id = :ingredientId")
    void deleteByIngredientId(@Param("ingredientId") Long ingredientId);
}