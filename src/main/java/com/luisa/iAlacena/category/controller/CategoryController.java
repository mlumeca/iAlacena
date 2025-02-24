package com.luisa.iAlacena.category.controller;

import com.luisa.iAlacena.category.dto.CreateCategoryRequest;
import com.luisa.iAlacena.category.dto.EditCategoryRequest;
import com.luisa.iAlacena.category.dto.CategoryResponse;
import com.luisa.iAlacena.category.model.Category;
import com.luisa.iAlacena.category.service.CategoryService;
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
@RequestMapping("/category")
@Tag(name = "Categoría", description = "El controlador de categorías.")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Creación de una nueva categoría",
            description = "Permite a un administrador crear una nueva categoría para ingredientes y recetas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Se ha creado la categoría",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CategoryResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "id": 1,
                                                        "name": "Carnes",
                                                        "parentCategoryId": null
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
    public ResponseEntity<CategoryResponse> createCategory(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateCategoryRequest request) {
        Category category = categoryService.createCategory(currentUser, request);
        return ResponseEntity.status(201).body(CategoryResponse.of(category));
    }

    @Operation(summary = "Editar una categoría existente",
            description = "Permite a un administrador actualizar el nombre o la relación de una categoría.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Categoría editada con éxito",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CategoryResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "id": 1,
                                                        "name": "Carnes Rojas",
                                                        "parentCategoryId": null
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
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Categoría no encontrada",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> editCategory(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            @Valid @RequestBody EditCategoryRequest request) {
        Category category = categoryService.editCategory(currentUser, id, request);
        return ResponseEntity.ok(CategoryResponse.of(category));
    }
}