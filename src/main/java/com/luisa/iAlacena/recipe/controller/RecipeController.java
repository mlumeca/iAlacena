package com.luisa.iAlacena.recipe.controller;

import com.luisa.iAlacena.category.dto.AssignCategoriesRequest;
import com.luisa.iAlacena.recipe.dto.CreateRecipeRequest;
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
        Recipe recipe = recipeService.createRecipe(currentUser, request);
        return ResponseEntity.status(201).body(RecipeResponse.of(recipe));
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
                                                                "categories": [
                                                                    {"id": 1, "name": "Carnes"},
                                                                    {"id": 2, "name": "Carbohidratos"}
                                                                ],
                                                                "userId": "550e8400-e29b-41d4-a716-446655440001"
                                                            },
                                                            {
                                                                "id": 2,
                                                                "name": "Ensalada César",
                                                                "description": "Ensalada fresca con pollo y aderezo César.",
                                                                "portions": 2,
                                                                "categories": [
                                                                    {"id": 1, "name": "Carnes"},
                                                                    {"id": 3, "name": "Vegetariana"}
                                                                ],
                                                                "userId": "550e8400-e29b-41d4-a716-446655440002"
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
    public ResponseEntity<ListRecipeResponse> getAllRecipes(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipePage = recipeService.getAllRecipes(currentUser, pageable, name, categoryId);
        return ResponseEntity.ok(ListRecipeResponse.of(recipePage));
    }

    @Operation(summary = "Asignar categorías a una receta",
            description = "Permite a un usuario autenticado asignar múltiples categorías a una receta existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Categorías asignadas con éxito",
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
                    description = "Datos inválidos o categorías no encontradas",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "No autenticado",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Receta no encontrada",
                    content = @Content)
    })
    @PutMapping("/{id}/categories")
    public ResponseEntity<RecipeResponse> assignCategories(
            @PathVariable Long id,
            @Valid @RequestBody AssignCategoriesRequest request) {
        Recipe recipe = recipeService.assignCategories(id, request);
        return ResponseEntity.ok(RecipeResponse.of(recipe));
    }
}
