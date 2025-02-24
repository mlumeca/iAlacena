package com.luisa.iAlacena.category.controller;

import com.luisa.iAlacena.category.dto.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Operation(summary = "Listar todas las categorías",
            description = "Permite a un usuario registrado ver todas las categorías disponibles con estructura jerárquica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Lista de categorías obtenida con éxito",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ListCategoryResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "totalElements": 3,
                                                        "totalPages": 2,
                                                        "pageNumber": 0,
                                                        "pageSize": 2,
                                                        "items": [
                                                            {
                                                                "id": 1,
                                                                "name": "Carnes",
                                                                "parentCategoryId": null
                                                            },
                                                            {
                                                                "id": 2,
                                                                "name": "Pollo",
                                                                "parentCategoryId": 1
                                                            }
                                                        ]
                                                    }
                                                    """
                                            )
                                    })
                    }),
            @ApiResponse(responseCode = "401",
                    description = "No autenticado",
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<ListCategoryResponse> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoryPage = categoryService.getAllCategories(pageable);
        return ResponseEntity.ok(ListCategoryResponse.of(categoryPage));
    }

    @Operation(summary = "Obtener detalles de una categoría específica",
            description = "Permite a un usuario registrado ver los detalles de una categoría, incluyendo subcategorías.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Categoría encontrada con éxito",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CategoryDetailResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "id": 1,
                                                        "name": "Carnes",
                                                        "parentCategoryId": null,
                                                        "childCategories": [
                                                            {
                                                                "id": 2,
                                                                "name": "Carnes Rojas",
                                                                "parentCategoryId": 1
                                                            },
                                                            {
                                                                "id": 3,
                                                                "name": "Carnes Blancas",
                                                                "parentCategoryId": 1
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
            @ApiResponse(responseCode = "404",
                    description = "Categoría no encontrada",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDetailResponse> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(CategoryDetailResponse.of(category));
    }
}