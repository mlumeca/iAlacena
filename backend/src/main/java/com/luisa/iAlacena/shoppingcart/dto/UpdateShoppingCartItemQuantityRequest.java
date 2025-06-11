package com.luisa.iAlacena.shoppingcart.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateShoppingCartItemQuantityRequest {

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}