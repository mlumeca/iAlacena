package com.luisa.iAlacena.shoppingcart.dto;

import com.luisa.iAlacena.shoppingcart.model.ShoppingCart;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ShoppingCartResponse(
        Long id,
        UUID userId,
        LocalDateTime createdAt,
        List<CartItemResponse> items
) {
    public static ShoppingCartResponse of(ShoppingCart cart) {
        return new ShoppingCartResponse(
                cart.getId(),
                cart.getUser().getId(),
                cart.getCreatedAt(),
                cart.getItems().entrySet().stream()
                        .map(entry -> new CartItemResponse(entry.getKey().getId(), entry.getValue()))
                        .toList()
        );
    }

    public record CartItemResponse(
            Long ingredientId,
            int quantity
    ) {}
}