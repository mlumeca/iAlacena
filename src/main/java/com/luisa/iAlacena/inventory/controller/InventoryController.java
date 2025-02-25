package com.luisa.iAlacena.inventory.controller;

import com.luisa.iAlacena.inventory.dto.AddInventoryRequest;
import com.luisa.iAlacena.inventory.dto.InventoryResponse;
import com.luisa.iAlacena.inventory.dto.UpdateInventoryRequest;
import com.luisa.iAlacena.inventory.service.InventoryService;
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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.UUID;

@RestController
@RequestMapping("/user")
@Tag(name = "Inventario", description = "El controlador del inventario virtual del usuario.")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Operation(summary = "Añadir un ingrediente al inventario",
            description = "Permite a un usuario registrado añadir un ingrediente a su inventario virtual.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Ingrediente añadido al inventario con éxito",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = InventoryResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "id": 1,
                                                        "userId": "550e8400-e29b-41d4-a716-446655440001",
                                                        "ingredientId": 1,
                                                        "quantity": 3,
                                                        "addedAt": "2025-02-25T10:00:00"
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
                    description = "Acceso denegado - Solo el propio usuario puede añadir a su inventario",
                    content = @Content)
    })
    @PostMapping("/{user_id}/inventory")
    public ResponseEntity<InventoryResponse> addIngredientToInventory(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("user_id") UUID userId,
            @Valid @RequestBody AddInventoryRequest request) {
        if (!currentUser.getId().equals(userId)) {
            throw new IllegalArgumentException("You can only add ingredients to your own inventory");
        }

        InventoryResponse inventoryResponse = inventoryService.addIngredientToInventory(userId, request);
        return ResponseEntity.status(201).body(inventoryResponse);
    }

    @Operation(summary = "Listar todos los ingredientes en el inventario",
            description = "Permite a un usuario registrado ver todos los ingredientes en su inventario con paginación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Lista de ingredientes en el inventario obtenida con éxito",
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
                                                                "ingredientId": 1,
                                                                "quantity": 3,
                                                                "addedAt": "2025-02-25T10:00:00"
                                                            },
                                                            {
                                                                "id": 2,
                                                                "userId": "550e8400-e29b-41d4-a716-446655440001",
                                                                "ingredientId": 2,
                                                                "quantity": 5,
                                                                "addedAt": "2025-02-25T11:00:00"
                                                            }
                                                        ],
                                                        "pageable": {
                                                            "pageNumber": 0,
                                                            "pageSize": 10,
                                                            "offset": 0,
                                                            "paged": true,
                                                            "unpaged": false
                                                        },
                                                        "totalElements": 2,
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
                    description = "Acceso denegado - Solo el propio usuario puede ver su inventario",
                    content = @Content)
    })
    @GetMapping("/{user_id}/inventory")
    public ResponseEntity<Page<InventoryResponse>> getAllInventoryItems(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("user_id") UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (!currentUser.getId().equals(userId)) {
            throw new IllegalArgumentException("You can only view your own inventory");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryResponse> inventoryPage = inventoryService.getAllInventoryItems(userId, pageable);
        return ResponseEntity.ok(inventoryPage);
    }

    @Operation(summary = "Actualizar la cantidad de un ingrediente en el inventario",
            description = "Permite a un usuario registrado actualizar la cantidad de un ingrediente en su inventario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Cantidad actualizada con éxito",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = InventoryResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "id": 1,
                                                        "userId": "550e8400-e29b-41d4-a716-446655440001",
                                                        "ingredientId": 1,
                                                        "quantity": 2,
                                                        "addedAt": "2025-02-25T10:00:00"
                                                    }
                                                    """
                                            )
                                    })
                    }),
            @ApiResponse(responseCode = "400",
                    description = "Usuario o ingrediente no encontrado en el inventario",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "No autenticado",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Acceso denegado - Solo el propio usuario puede actualizar su inventario",
                    content = @Content)
    })
    @PutMapping("/{user_id}/inventory/{ingredient_id}")
    public ResponseEntity<InventoryResponse> updateIngredientQuantity(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("user_id") UUID userId,
            @PathVariable("ingredient_id") Long ingredientId,
            @Valid @RequestBody UpdateInventoryRequest request) {
        if (!currentUser.getId().equals(userId)) {
            throw new IllegalArgumentException("You can only update your own inventory");
        }

        InventoryResponse updatedInventory = inventoryService.updateIngredientQuantity(userId, ingredientId, request);
        return ResponseEntity.ok(updatedInventory);
    }
}