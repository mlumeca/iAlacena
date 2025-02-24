package com.luisa.iAlacena.recipe.controller;

import com.luisa.iAlacena.recipe.dto.CreateRecipeRequest;
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
                                                        "ingredients": ["pollo", "curry", "arroz", "cebolla"],
                                                        "categories": ["Carnes", "Carbohidratos"],
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
}