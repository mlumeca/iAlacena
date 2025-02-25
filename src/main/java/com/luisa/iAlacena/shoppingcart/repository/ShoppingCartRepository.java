package com.luisa.iAlacena.shoppingcart.repository;

import com.luisa.iAlacena.shoppingcart.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
}