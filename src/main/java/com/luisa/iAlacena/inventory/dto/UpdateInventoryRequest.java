package com.luisa.iAlacena.inventory.dto;

import jakarta.validation.constraints.Min;

public record UpdateInventoryRequest(
        @Min(value = 0, message = "quantity.min")
        int quantity
) {}