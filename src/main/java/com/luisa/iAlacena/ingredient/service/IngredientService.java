package com.luisa.iAlacena.ingredient.service;

import com.luisa.iAlacena.category.dto.AssignCategoriesRequest;
import com.luisa.iAlacena.category.model.Category;
import com.luisa.iAlacena.category.repository.CategoryRepository;
import com.luisa.iAlacena.ingredient.dto.CreateIngredientRequest;
import com.luisa.iAlacena.ingredient.dto.EditIngredientRequest;
import com.luisa.iAlacena.ingredient.model.Ingredient;
import com.luisa.iAlacena.ingredient.repository.IngredientRepository;
import com.luisa.iAlacena.inventory.repository.InventoryRepository;
import com.luisa.iAlacena.recipe.repository.RecipeIngredientRepository;
import com.luisa.iAlacena.shoppingcart.repository.ShoppingCartItemRepository;
import com.luisa.iAlacena.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;
    private final ShoppingCartItemRepository shoppingCartItemRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;

    public IngredientService(IngredientRepository ingredientRepository,
                             CategoryRepository categoryRepository,
                             InventoryRepository inventoryRepository,
                             ShoppingCartItemRepository shoppingCartItemRepository,
                             RecipeIngredientRepository recipeIngredientRepository) {
        this.ingredientRepository = ingredientRepository;
        this.categoryRepository = categoryRepository;
        this.inventoryRepository = inventoryRepository;
        this.shoppingCartItemRepository = shoppingCartItemRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
    }

    public Ingredient createIngredient(User currentUser, CreateIngredientRequest request) {
        if (!currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Only administrators can create ingredients");
        }

        if (ingredientRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Ingredient with name '" + request.name() + "' already exists");
        }

        Ingredient ingredient = Ingredient.builder()
                .name(request.name())
                .quantity(1)
                .unitOfMeasure(request.unitOfMeasure())
                .build();

        return ingredientRepository.save(ingredient);
    }

    public Ingredient assignCategories(Long id, AssignCategoriesRequest request) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found with id: " + id));

        Set<Category> categories = new HashSet<>(categoryRepository.findAllById(request.categoryIds()));
        if (categories.size() != request.categoryIds().size()) {
            throw new IllegalArgumentException("One or more category IDs do not exist");
        }

        ingredient.setCategories(categories);
        return ingredientRepository.save(ingredient);
    }

    public Ingredient editIngredient(User currentUser, Long id, EditIngredientRequest request) {
        if (!currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Only administrators can edit ingredients");
        }

        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found with id: " + id));

        if (!request.name().equals(ingredient.getName()) && ingredientRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Ingredient with name '" + request.name() + "' already exists");
        }

        ingredient.setName(request.name());
        if (request.categoryIds() != null) {
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(request.categoryIds()));
            if (categories.size() != request.categoryIds().size()) {
                throw new IllegalArgumentException("One or more category IDs do not exist");
            }
            ingredient.setCategories(categories);
        }

        return ingredientRepository.save(ingredient);
    }

    public Page<Ingredient> getAllIngredients(User currentUser, Pageable pageable, String name, Long categoryId) {
        if (!currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Only administrators can view all ingredients");
        }

        if (name != null && !name.isEmpty() && categoryId != null) {
            return ingredientRepository.findByNameContainingIgnoreCaseAndCategoryId(name, categoryId, pageable);
        } else if (name != null && !name.isEmpty()) {
            return ingredientRepository.findByNameContainingIgnoreCase(name, pageable);
        } else if (categoryId != null) {
            return ingredientRepository.findByCategoryId(categoryId, pageable);
        } else {
            return ingredientRepository.findAllWithCategories(pageable);
        }
    }

    public Ingredient getIngredientById(User currentUser, Long id) {
        if (!currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Only administrators can view ingredient details");
        }

        return ingredientRepository.findByIdWithCategories(id)
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found with id: " + id));
    }

    @Transactional
    public void deleteIngredient(User currentUser, Long id) {
        if (!currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Only administrators can delete ingredients");
        }

        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found with id: " + id));

        recipeIngredientRepository.deleteByIngredientId(id);
        inventoryRepository.deleteByIngredientId(id);
        shoppingCartItemRepository.deleteByIngredientId(id);
        ingredientRepository.delete(ingredient);
    }
}