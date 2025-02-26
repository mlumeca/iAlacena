package com.luisa.iAlacena.recipe.controller;

import com.luisa.iAlacena.category.dto.AssignCategoriesRequest;
import com.luisa.iAlacena.recipe.dto.CreateRecipeRequest;
import com.luisa.iAlacena.recipe.dto.EditRecipeRequest;
import com.luisa.iAlacena.recipe.dto.ListRecipeResponse;
import com.luisa.iAlacena.recipe.dto.RecipeResponse;
import com.luisa.iAlacena.recipe.model.Recipe;
import com.luisa.iAlacena.recipe.service.RecipeService;
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

@RestController
@RequestMapping("/recipe")
@Tag(name = "Receta", description = "El controlador de recetas.")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @Operation(summary = "Crear una nueva receta",
            description = "Permite a un usuario autenticado crear una nueva receta y añadirla a su colección.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Receta creada con éxito",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = RecipeResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "id": 1,
                                                        "name": "Pollo al Curry",
                                                        "description": "Un delicioso plato de pollo con curry y arroz.",
                                                        "portions": 4,
                                                        "createdAt": "2025-02-25T10:00:00",
                                                        "updatedAt": "2025-02-25T10:00:00",
                                                        "imgUrl": "http://example.com/pollo-curry.jpg",
                                                        "ingredients": [
                                                            {"id": 1, "name": "Pollo", "quantity": 1, "unitOfMeasure": "KILO"},
                                                            {"id": 2, "name": "Curry", "quantity": 1, "unitOfMeasure": "UNIDAD"}
                                                        ],
                                                        "categories": [
                                                            {"id": 1, "name": "Carnes"},
                                                            {"id": 2, "name": "Carbohidratos"}
                                                        ],
                                                        "userId": "550e8400-e29b-41d4-a716-446655440001"
                                                    }
                                                    """
                                            )
                                    })
                    }),
            @ApiResponse(responseCode = "400",
                    description = "Datos inválidos",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "No autenticado",
                    content = @Content)
    })
    @PostMapping("/create")
    public ResponseEntity<RecipeResponse> createRecipe(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateRecipeRequest request) {
        RecipeResponse response = recipeService.createRecipe(currentUser, request);
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "Listar todas las recetas",
            description = "Permite a un administrador ver todas las recetas disponibles con filtros y paginación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Lista de recetas obtenida con éxito",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ListRecipeResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "totalElements": 2,
                                                        "totalPages": 1,
                                                        "pageNumber": 0,
                                                        "pageSize": 10,
                                                        "items": [
                                                            {
                                                                "id": 1,
                                                                "name": "Pollo al Curry",
                                                                "description": "Un delicioso plato de pollo con curry y arroz.",
                                                                "portions": 4,
                                                                "createdAt": "2025-02-25T10:00:00",
                                                                "updatedAt": "2025-02-25T10:00:00",
                                                                "imgUrl": "http://example.com/pollo-curry.jpg",
                                                                "ingredients": [
                                                                    {"id": 1, "name": "Pollo", "quantity": 1, "unitOfMeasure": "KILO"},
                                                                    {"id": 2, "name": "Curry", "quantity": 1, "unitOfMeasure": "UNIDAD"}
                                                                ],
                                                                "categories": [
                                                                    {"id": 1, "name": "Carnes"},
                                                                    {"id": 2, "name": "Carbohidratos"}
                                                                ],
                                                                "userId": "550e8400-e29b-41d4-a716-446655440001"
                                                            }
                                                        ]
                                                    }
                                                    """
                                            )
                                    })
                    }),
            @ApiResponse(responseCode = "401",
                    description = "No autenticado",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Acceso denegado - Requiere rol ADMIN",
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<Page<RecipeResponse>> getAllRecipes(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RecipeResponse> recipes = recipeService.getAllRecipes(currentUser, pageable, name, categoryId);
        return ResponseEntity.ok(recipes);
    }

    @Operation(summary = "Obtener detalles de una receta específica",
            description = "Permite a un usuario registrado ver todos los detalles de una receta, incluyendo ingredientes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Receta encontrada con éxito",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = RecipeResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "id": 1,
                                                        "name": "Pollo al Curry",
                                                        "description": "Un delicioso plato de pollo con curry y arroz.",
                                                        "portions": 4,
                                                        "createdAt": "2025-02-25T10:00:00",
                                                        "updatedAt": "2025-02-25T10:00:00",
                                                        "imgUrl": "http://example.com/pollo-curry.jpg",
                                                        "ingredients": [
                                                            {"id": 1, "name": "Pollo", "quantity": 1, "unitOfMeasure": "KILO"},
                                                            {"id": 2, "name": "Curry", "quantity": 1, "unitOfMeasure": "UNIDAD"}
                                                        ],
                                                        "categories": [
                                                            {"id": 1, "name": "Carnes"},
                                                            {"id": 2, "name": "Carbohidratos"}
                                                        ],
                                                        "userId": "550e8400-e29b-41d4-a716-446655440001"
                                                    }
                                                    """
                                            )
                                    })
                    }),
            @ApiResponse(responseCode = "401",
                    description = "No autenticado",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Receta no encontrada",
                    content = @Content)
    })
    public ResponseEntity<RecipeResponse> getRecipeById(@PathVariable("id") Long id) {
        RecipeResponse response = recipeService.getRecipeById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Editar una receta existente",
            description = "Permite al creador de la receta actualizar sus detalles, como nombre, descripción, porciones, imagen, categorías o ingredientes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Receta actualizada con éxito",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = RecipeResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "id": 1,
                                                        "name": "Pollo al Curry Mejorado",
                                                        "description": "Un plato aún más sabroso con curry, arroz y especias.",
                                                        "portions": 5,
                                                        "createdAt": "2025-02-25T10:00:00",
                                                        "updatedAt": "2025-02-25T12:00:00",
                                                        "imgUrl": "http://example.com/pollo-curry-v2.jpg",
                                                        "ingredients": [
                                                            {"id": 1, "name": "Pollo", "quantity": 1, "unitOfMeasure": "KILO"},
                                                            {"id": 3, "name": "Arroz", "quantity": 2, "unitOfMeasure": "KILO"}
                                                        ],
                                                        "categories": [
                                                            {"id": 1, "name": "Carnes"},
                                                            {"id": 2, "name": "Carbohidratos"}
                                                        ],
                                                        "userId": "550e8400-e29b-41d4-a716-446655440001"
                                                    }
                                                    """
                                            )
                                    })
                    }),
            @ApiResponse(responseCode = "400",
                    description = "Datos inválidos o IDs de categorías/ingredientes no encontrados",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "No autenticado",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Acceso denegado - Solo el creador puede editar la receta",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Receta no encontrada",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<RecipeResponse> editRecipe(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("id") Long id,
            @Valid @RequestBody EditRecipeRequest request) {
        RecipeResponse response = recipeService.editRecipe(currentUser, id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar una receta",
            description = "Permite a un usuario eliminar una receta de su colección, siempre que sea el creador de la misma.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Receta eliminada con éxito", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo el creador puede eliminar la receta", content = @Content),
            @ApiResponse(responseCode = "404", description = "Receta no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        recipeService.deleteRecipe(currentUser, id);
        return ResponseEntity.noContent().build();
    }
}