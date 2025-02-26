package com.luisa.iAlacena.shoppingcart.repository;

import com.luisa.iAlacena.shoppingcart.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    @Query("""
            SELECT sc
            FROM ShoppingCart sc
            WHERE sc.user.id = :userId
            """)
    Optional<ShoppingCart> findByUserId(@Param("userId") UUID userId);
}