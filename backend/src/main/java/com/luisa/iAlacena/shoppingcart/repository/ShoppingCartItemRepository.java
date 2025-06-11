package com.luisa.iAlacena.shoppingcart.repository;

import com.luisa.iAlacena.shoppingcart.model.ShoppingCartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ShoppingCartItemRepository extends JpaRepository<ShoppingCartItem, Long> {

    @Modifying
    @Query("DELETE FROM ShoppingCartItem sci WHERE sci.ingredient.id = :ingredientId")
    void deleteByIngredientId(@Param("ingredientId") Long ingredientId);

    @Query("SELECT sci FROM ShoppingCartItem sci WHERE sci.shoppingCart.id = :shoppingCartId AND sci.ingredient.id = :ingredientId")
    Optional<ShoppingCartItem> findByShoppingCartIdAndIngredientId(
            @Param("shoppingCartId") Long shoppingCartId,
            @Param("ingredientId") Long ingredientId);
}