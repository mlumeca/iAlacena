package com.luisa.iAlacena.shoppingcart.repository;

import com.luisa.iAlacena.shoppingcart.model.ShoppingCartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShoppingCartItemRepository extends JpaRepository<ShoppingCartItem, Long> {

    @Modifying
    @Query("DELETE FROM ShoppingCartItem sci WHERE sci.ingredient.id = :ingredientId")
    void deleteByIngredientId(@Param("ingredientId") Long ingredientId);
}