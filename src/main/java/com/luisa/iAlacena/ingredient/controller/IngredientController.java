package com.luisa.iAlacena.ingredient.controller;

import com.luisa.iAlacena.category.dto.AssignCategoriesRequest;
import com.luisa.iAlacena.ingredient.dto.CreateIngredientRequest;
import com.luisa.iAlacena.ingredient.dto.EditIngredientRequest;
import com.luisa.iAlacena.ingredient.dto.IngredientResponse;
import com.luisa.iAlacena.ingredient.model.Ingredient;
import com.luisa.iAlacena.ingredient.service.IngredientService;
import com.luisa.iAlacena.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/ingredient")
@Tag(name = "Ingrediente", description = "El controlador de ingredientes.")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @Operation(summary = "Crear un nuevo ingrediente",
            description = "Permite a un administrador crear un nuevo ingrediente para usar en recetas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Ingrediente creado con éxito",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = IngredientResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "id": 1,
                                                        "name": "Pollo",
                                                        "quantity": 1,
                                                        "unitOfMeasure": "KILO"
                                                    }
                                                    """
                                            )
                                    })
                    }),
            @ApiResponse(responseCode = "400",
                    description = "Datos inválidos o nombre duplicado",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "No autenticado",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Acceso denegado - Requiere rol ADMIN",
                    content = @Content)
    })
    @PostMapping("/create")
    public ResponseEntity<IngredientResponse> createIngredient(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateIngredientRequest request) {
        Ingredient ingredient = ingredientService.createIngredient(currentUser, request);
        return ResponseEntity.status(201).body(IngredientResponse.of(ingredient));
    }

    @Operation(summary = "Editar un ingrediente existente",
            description = "Permite a un administrador actualizar el nombre o las categorías de un ingrediente existente. La cantidad no puede modificarse en este endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Ingrediente actualizado con éxito",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = IngredientResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "id": 1,
                                                        "name": "Pollo Fresco",
                                                        "quantity": 1,
                                                        "unitOfMeasure": "KILO",
                                                        "categories": [
                                                            {"id": 1, "name": "Carnes"},
                                                            {"id": 2, "name": "Proteínas"}
                                                        ]
                                                    }
                                                    """
                                            )
                                    })
                    }),
            @ApiResponse(responseCode = "400",
                    description = "Datos inválidos, nombre duplicado o categorías no encontradas",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "No autenticado",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Acceso denegado - Requiere rol ADMIN",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Ingrediente no encontrado",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<IngredientResponse> editIngredient(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            @Valid @RequestBody EditIngredientRequest request) {
        Ingredient updatedIngredient = ingredientService.editIngredient(currentUser, id, request);
        return ResponseEntity.ok(IngredientResponse.of(updatedIngredient));
    }
}