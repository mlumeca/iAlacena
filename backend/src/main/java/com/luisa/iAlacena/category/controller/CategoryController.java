package com.luisa.iAlacena.category.controller;

import com.luisa.iAlacena.category.dto.*;
import com.luisa.iAlacena.category.model.Category;
import com.luisa.iAlacena.category.service.CategoryService;
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
    private final IngredientService ingredientService;

    public CategoryController(CategoryService categoryService, IngredientService ingredientService) {
        this.categoryService = categoryService;
        this.ingredientService = ingredientService;
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

    @Operation(summary = "Asignar categorías a un ingrediente",
            description = "Permite a un usuario autenticado asignar múltiples categorías a un ingrediente existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Categorías asignadas con éxito",
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
                    description = "Datos inválidos o categorías no encontradas",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "No autenticado",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Ingrediente no encontrado",
                    content = @Content)
    })
    @PutMapping("/{id}/categories")
    public ResponseEntity<IngredientResponse> assignCategories(
            @PathVariable Long id,
            @Valid @RequestBody AssignCategoriesRequest request) {
        Ingredient ingredient = ingredientService.assignCategories(id, request);
        return ResponseEntity.ok(IngredientResponse.of(ingredient));
    }

    @Operation(summary = "Crear una subcategoría dentro de una categoría existente",
            description = "Permite a un administrador crear una nueva subcategoría bajo una categoría padre especificada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Subcategoría creada con éxito",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CategoryResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                    {
                                                        "id": 12,
                                                        "name": "Pollo",
                                                        "parentCategoryId": 1
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
                    description = "Categoría padre no encontrada",
                    content = @Content)
    })
    @PostMapping("/{parentId}/subcategory")
    public ResponseEntity<CategoryResponse> createSubcategory(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long parentId,
            @Valid @RequestBody CreateSubcategoryRequest request) {
        Category subcategory = categoryService.createSubcategory(currentUser, parentId, request);
        return ResponseEntity.status(201).body(CategoryResponse.of(subcategory));
    }

    @Operation(summary = "Reasignar la categoría padre de una categoría existente",
            description = "Permite a un administrador cambiar la categoría padre de una categoría por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Categoría padre reasignada con éxito",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CategoryResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                {
                                                    "id": 2,
                                                    "name": "Carnes Rojas",
                                                    "parentCategoryId": 7
                                                }
                                                """
                                            )
                                    })
                    }),
            @ApiResponse(responseCode = "404",
                    description = "Categoría o nueva categoría padre no encontrada por ID",
                    content = @Content)
    })
    @PutMapping("/{categoryId}/parent")
    public ResponseEntity<CategoryResponse> reassignParentCategory(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long categoryId,
            @Valid @RequestBody ReassignParentRequest request) {
        Category category = categoryService.reassignParentCategory(currentUser, categoryId, request);
        return ResponseEntity.ok(CategoryResponse.of(category));
    }

    @Operation(summary = "Eliminar una categoría existente",
            description = "Permite a un administrador eliminar una categoría. Las subcategorías asociadas quedarán sin categoría padre, y la categoría se eliminará de ingredientes y recetas sin afectar categorías padre.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Categoría eliminada con éxito",
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        categoryService.deleteCategory(currentUser, id);
        return ResponseEntity.noContent().build();
    }
}