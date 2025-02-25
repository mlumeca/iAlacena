package com.luisa.iAlacena.favorites.controller;

import com.luisa.iAlacena.favorites.dto.AddFavoriteRequest;
import com.luisa.iAlacena.favorites.dto.FavoritesResponse;
import com.luisa.iAlacena.favorites.service.FavoritesService;
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
@Tag(name = "Favoritos", description = "El controlador de recetas favoritas.")
public class FavoritesController {

    private final FavoritesService favoritesService;

    public FavoritesController(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    @Operation(summary = "Añadir una receta a favoritos",
            description = "Permite a un usuario registrado añadir una receta a su lista de favoritos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Receta añadida a favoritos con éxito",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = FavoritesResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "id": 1,
                                                        "userId": "550e8400-e29b-41d4-a716-446655440001",
                                                        "recipeId": 1,
                                                        "addedAt": "2025-02-25T10:00:00"
                                                    }
                                                    """
                                            )
                                    })
                    }),
            @ApiResponse(responseCode = "400",
                    description = "Usuario o receta no encontrados",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "No autenticado",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Acceso denegado - Solo el propio usuario puede añadir a sus favoritos",
                    content = @Content)
    })
    @PostMapping("/{user_id}/favorites")
    public ResponseEntity<FavoritesResponse> addRecipeToFavorites(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("user_id") UUID userId,
            @Valid @RequestBody AddFavoriteRequest request) {
        if (!currentUser.getId().equals(userId)) {
            throw new IllegalArgumentException("You can only add recipes to your own favorites list");
        }

        FavoritesResponse favorite = favoritesService.addRecipeToFavorites(userId, request);
        return ResponseEntity.status(201).body(favorite);
    }

    @Operation(summary = "Eliminar una receta de favoritos",
            description = "Permite a un usuario registrado eliminar una receta de su lista de favoritos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Receta eliminada de favoritos con éxito",
                    content = @Content),
            @ApiResponse(responseCode = "400",
                    description = "Usuario, receta o relación no encontrada",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "No autenticado",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Acceso denegado - Solo el propio usuario puede eliminar de sus favoritos",
                    content = @Content)
    })
    @DeleteMapping("/{user_id}/favorites/{recipe_id}")
    public ResponseEntity<Void> removeRecipeFromFavorites(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("user_id") UUID userId,
            @PathVariable("recipe_id") Long recipeId) {
        if (!currentUser.getId().equals(userId)) {
            throw new IllegalArgumentException("You can only remove recipes from your own favorites list");
        }

        favoritesService.removeRecipeFromFavorites(userId, recipeId);
        return ResponseEntity.noContent().build();
    }
}