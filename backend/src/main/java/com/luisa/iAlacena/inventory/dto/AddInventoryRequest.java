package com.luisa.iAlacena.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddInventoryRequest(
        @NotNull(message = "ingredientId.required")
        Long ingredientId,

        @Min(value = 1, message = "quantity.min")
        int quantity
) {}