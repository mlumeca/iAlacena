package com.luisa.iAlacena.inventory.dto;

import com.luisa.iAlacena.inventory.model.Inventory;

import java.time.LocalDateTime;
import java.util.UUID;

public record InventoryResponse(
        Long id,
        UUID userId,
        Long ingredientId,
        int quantity,
        LocalDateTime addedAt
) {
    public static InventoryResponse of(Inventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getUser().getId(),
                inventory.getIngredient().getId(),
                inventory.getQuantity(),
                inventory.getAddedAt()
        );
    }
}