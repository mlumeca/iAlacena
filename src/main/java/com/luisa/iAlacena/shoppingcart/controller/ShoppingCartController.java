package com.luisa.iAlacena.shoppingcart.controller;

import com.luisa.iAlacena.shoppingcart.dto.AddToCartRequest;
import com.luisa.iAlacena.shoppingcart.dto.ShoppingCartResponse;
import com.luisa.iAlacena.shoppingcart.service.ShoppingCartService;
import com.luisa.iAlacena.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
@Tag(name = "Carrito de Compra", description = "El controlador del carrito de compra del usuario.")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @Operation(summary = "Añadir un ingrediente al carrito de compra",
            description = "Permite a un usuario registrado añadir un ingrediente a su carrito de compra. Si el ingrediente ya existe, aumenta su cantidad en 1; si no, lo añade con cantidad 1.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Ingrediente añadido al carrito con éxito",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ShoppingCartResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "id": 1,
                                                        "userId": "550e8400-e29b-41d4-a716-446655440001",
                                                        "createdAt": "2025-02-25T10:00:00",
                                                        "items": [
                                                            {
                                                                "ingredientId": 1,
                                                                "quantity": 1
                                                            }
                                                        ]
                                                    }
                                                    """
                                            )
                                    })
                    }),
            @ApiResponse(responseCode = "400",
                    description = "Usuario o ingrediente no encontrados",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "No autenticado",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Acceso denegado - Solo el propio usuario puede modificar su carrito",
                    content = @Content)
    })
    @PostMapping("/{user_id}/shopping-cart")
    public ResponseEntity<ShoppingCartResponse> addIngredientToCart(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("user_id") UUID userId,
            @Valid @RequestBody AddToCartRequest request) {
        if (!currentUser.getId().equals(userId)) {
            throw new IllegalArgumentException("You can only add ingredients to your own shopping cart");
        }

        ShoppingCartResponse response = shoppingCartService.addIngredientToCart(userId, request);
        return ResponseEntity.ok(response);
    }
}