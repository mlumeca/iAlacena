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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/recipe")
@Tag(name = "Receta", description = "El controlador de recetas.")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @Operation(summary = "Crear una nueva receta",
            description = "Permite a un usuario autenticado crear una nueva receta sin imagen.")
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
                                                        "createdAt": "2025-06-17T21:27:00",
                                                        "updatedAt": "2025-06-17T21:27:00",
                                                        "imgUrl": null,
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
        Recipe recipe = recipeService.createRecipe(currentUser, request);
        return ResponseEntity.status(201).body(RecipeResponse.of(recipe));
    }

    @Operation(summary = "Actualizar la imagen de una receta",
            description = "Permite al creador de la receta añadir o actualizar la imagen asociada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Imagen actualizada con éxito",
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
                                                        "createdAt": "2025-06-17T21:27:00",
                                                        "updatedAt": "2025-06-17T21:30:00",
                                                        "imgUrl": "http://localhost:8080/download/pollo-curry.jpg",
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
                    description = "Archivo no válido",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "No autenticado",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Acceso denegado - Solo el creador puede actualizar la imagen",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Receta no encontrada",
                    content = @Content)
    })
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RecipeResponse> updateRecipeImage(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) {
        Recipe updatedRecipe = recipeService.updateRecipeImage(currentUser, id, file);
        return ResponseEntity.ok(RecipeResponse.of(updatedRecipe));
    }

    @Operation(summary = "Listar todas las recetas",
            description = "Permite a un usuario ver todas las recetas disponibles con filtros y paginación.")
    @ApiResponses(value = {
            // Existing API responses unchanged
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

    @Operation(summary = "Obtener detalles de una receta específica",
            description = "Permite ver todos los detalles de una receta, incluyendo ingredientes, categorías e imagen.")
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
                                                        "createdAt": "2025-06-17T10:00:00",
                                                        "updatedAt": "2025-06-17T10:00:00",
                                                        "imgUrl": "http://localhost:8080/download/pollo-curry.jpg",
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
            @ApiResponse(responseCode = "404",
                    description = "Receta no encontrada",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponse> getRecipeById(@PathVariable Long id) {
        Recipe recipe = recipeService.getRecipeById(id);
        return ResponseEntity.ok(RecipeResponse.of(recipe));
    }

    @Operation(summary = "Editar una receta existente",
            description = "Permite al creador de la receta actualizar sus detalles, con la opción de actualizar la imagen.")
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
                                                        "imgUrl": "http://localhost:8080/download/pollo-curry-v2.jpg",
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
                    description = "Datos inválidos, archivo no válido o IDs de categorías/ingredientes no encontrados",
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
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RecipeResponse> editRecipe(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            @Valid @RequestPart("data") EditRecipeRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        Recipe updatedRecipe = recipeService.editRecipe(currentUser, id, request, file);
        return ResponseEntity.ok(RecipeResponse.of(updatedRecipe));
    }

    @Operation(summary = "Eliminar una receta",
            description = "Permite a un usuario eliminar una receta de su colección, siempre que sea el creador o admin.")
    @ApiResponses(value = {
            // Existing API responses unchanged
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        recipeService.deleteRecipe(currentUser, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar la imagen de una receta",
            description = "Permite al creador de la receta eliminar su imagen asociada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Imagen de la receta eliminada con éxito",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "No autenticado",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Acceso denegado - Solo el creador puede eliminar la imagen",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Receta no encontrada",
                    content = @Content)
    })
    @DeleteMapping("/{id}/image")
    public ResponseEntity<Void> deleteRecipeImage(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        recipeService.deleteRecipeImage(currentUser, id);
        return ResponseEntity.noContent().build();
    }
}