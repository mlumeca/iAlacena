package com.luisa.iAlacena.shoppingcart.dto;

import jakarta.validation.constraints.NotNull;

public record AddToCartRequest(
        @NotNull(message = "ingredientId.required")
        Long ingredientId
) {}