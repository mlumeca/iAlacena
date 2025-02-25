package com.luisa.iAlacena.inventory.repository;

import com.luisa.iAlacena.inventory.model.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query("""
            SELECT i
            FROM Inventory i
            LEFT JOIN FETCH i.ingredient
            WHERE i.user.id = :userId
            """)
    Page<Inventory> findByUserId(@Param("userId") UUID userId, Pageable pageable);
}