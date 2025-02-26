package com.luisa.iAlacena.shoppingcart.controller;

import com.luisa.iAlacena.shoppingcart.dto.AddToCartRequest;
import com.luisa.iAlacena.shoppingcart.dto.ShoppingCartResponse;
import com.luisa.iAlacena.shoppingcart.dto.UpdateShoppingCartItemQuantityRequest;
import com.luisa.iAlacena.shoppingcart.model.ShoppingCartItem;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user/{user_id}/shopping-cart")  // Ajustado para incluir {user_id}/shopping-cart
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
    @PostMapping
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

    @Operation(summary = "Eliminar un ingrediente del carrito de compra",
            description = "Permite a un usuario registrado eliminar un ingrediente de su carrito de compra. Reduce la cantidad en 1; si la cantidad llega a 0 o menos, elimina el ingrediente del carrito.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Ingrediente eliminado o cantidad reducida con éxito",
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
                    description = "Usuario o ingrediente no encontrado en el carrito",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "No autenticado",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Acceso denegado - Solo el propio usuario puede modificar su carrito",
                    content = @Content)
    })
    @DeleteMapping("/{ingredient_id}")
    public ResponseEntity<ShoppingCartResponse> removeIngredientFromCart(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("user_id") UUID userId,
            @PathVariable("ingredient_id") Long ingredientId) {
        if (!currentUser.getId().equals(userId)) {
            throw new IllegalArgumentException("You can only remove ingredients from your own shopping cart");
        }

        ShoppingCartResponse response = shoppingCartService.removeIngredientFromCart(userId, ingredientId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Ver el contenido del carrito de compra",
            description = "Permite a un usuario registrado ver todos los ingredientes en su carrito de compra con sus cantidades, con soporte para paginación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Contenido del carrito obtenido con éxito",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Page.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "content": [
                                                            {
                                                                "id": 1,
                                                                "userId": "550e8400-e29b-41d4-a716-446655440001",
                                                                "createdAt": "2025-02-25T10:00:00",
                                                                "items": [
                                                                    {
                                                                        "ingredientId": 1,
                                                                        "quantity": 2
                                                                    },
                                                                    {
                                                                        "ingredientId": 2,
                                                                        "quantity": 1
                                                                    }
                                                                ]
                                                            }
                                                        ],
                                                        "pageable": {
                                                            "pageNumber": 0,
                                                            "pageSize": 10,
                                                            "offset": 0,
                                                            "paged": true,
                                                            "unpaged": false
                                                        },
                                                        "totalElements": 1,
                                                        "totalPages": 1,
                                                        "size": 10,
                                                        "number": 0
                                                    }
                                                    """
                                            )
                                    })
                    }),
            @ApiResponse(responseCode = "400",
                    description = "Usuario no encontrado",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "No autenticado",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Acceso denegado - Solo el propio usuario puede ver su carrito",
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<Page<ShoppingCartResponse>> getCartContents(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("user_id") UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (!currentUser.getId().equals(userId)) {
            throw new IllegalArgumentException("You can only view your own shopping cart");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<ShoppingCartResponse> cartPage = shoppingCartService.getCartContents(userId, pageable);
        return ResponseEntity.ok(cartPage);
    }

    @Operation(summary = "Vaciar el carrito de compra",
            description = "Permite a un usuario registrado eliminar todos los ingredientes de su carrito de compra.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Carrito vaciado con éxito",
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
                                                        "items": []
                                                    }
                                                    """
                                            )
                                    })
                    }),
            @ApiResponse(responseCode = "400",
                    description = "Usuario no encontrado",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "No autenticado",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Acceso denegado - Solo el propio usuario puede vaciar su carrito",
                    content = @Content)
    })
    @DeleteMapping
    public ResponseEntity<ShoppingCartResponse> clearCart(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("user_id") UUID userId) {
        if (!currentUser.getId().equals(userId)) {
            throw new IllegalArgumentException("You can only clear your own shopping cart");
        }

        ShoppingCartResponse response = shoppingCartService.clearCart(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Actualizar la cantidad de un ingrediente en el carrito",
            description = "Permite a un usuario actualizar la cantidad de un ingrediente específico en su carrito de compra a un valor exacto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Cantidad actualizada con éxito",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ShoppingCartItem.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "id": 1,
                                                        "shoppingCartId": 1,
                                                        "ingredientId": 1,
                                                        "quantity": 5
                                                    }
                                                    """
                                            )
                                    })
                    }),
            @ApiResponse(responseCode = "400",
                    description = "Ingrediente no encontrado en el carrito o datos inválidos",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "No autenticado",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Acceso denegado - Solo el propietario del carrito puede modificarlo",
                    content = @Content)
    })
    @PutMapping("/item/{ingredient_id}")
    public ResponseEntity<ShoppingCartItem> updateShoppingCartItemQuantity(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("user_id") UUID userId,
            @PathVariable("ingredient_id") Long ingredientId,
            @Valid @RequestBody UpdateShoppingCartItemQuantityRequest request) {
        if (!currentUser.getId().equals(userId)) {
            throw new IllegalArgumentException("You can only update your own shopping cart");
        }

        ShoppingCartItem updatedItem = shoppingCartService.updateItemQuantity(userId, ingredientId, request.getQuantity());
        return ResponseEntity.ok(updatedItem);
    }
}