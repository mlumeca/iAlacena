package com.luisa.iAlacena.recipe.service;

import com.luisa.iAlacena.category.dto.AssignCategoriesRequest;
import com.luisa.iAlacena.category.model.Category;
import com.luisa.iAlacena.category.repository.CategoryRepository;
import com.luisa.iAlacena.ingredient.model.Ingredient;
import com.luisa.iAlacena.ingredient.repository.IngredientRepository;
import com.luisa.iAlacena.recipe.dto.CreateRecipeRequest;
import com.luisa.iAlacena.recipe.dto.EditRecipeRequest;
import com.luisa.iAlacena.recipe.model.Recipe;
import com.luisa.iAlacena.recipe.repository.RecipeRepository;
import com.luisa.iAlacena.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;

    public RecipeService(RecipeRepository recipeRepository, CategoryRepository categoryRepository, IngredientRepository ingredientRepository) {
        this.recipeRepository = recipeRepository;
        this.categoryRepository = categoryRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public Recipe createRecipe(User currentUser, CreateRecipeRequest request) {
        Set<Category> categories = request.categoryIds() != null && !request.categoryIds().isEmpty()
                ? new HashSet<>(categoryRepository.findAllById(request.categoryIds())) // Convert List to Set
                : Set.of(); // Empty Set if no categories

        if (request.categoryIds() != null && categories.size() != request.categoryIds().size()) {
            throw new IllegalArgumentException("One or more category IDs do not exist");
        }

        Recipe recipe = Recipe.builder()
                .name(request.name())
                .description(request.description())
                .portions(request.portions())
                .categories(categories)
                .user(currentUser)
                .build();
        return recipeRepository.save(recipe);
    }

    public Page<Recipe> getAllRecipes(User currentUser, Pageable pageable, String name, Long categoryId) {
        if (!currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Only administrators can view all recipes");
        }

        if (name != null && !name.isEmpty() && categoryId != null) {
            return recipeRepository.findByNameContainingIgnoreCaseAndCategoryId(name, categoryId, pageable);
        } else if (name != null && !name.isEmpty()) {
            return recipeRepository.findByNameContainingIgnoreCase(name, pageable);
        } else if (categoryId != null) {
            return recipeRepository.findByCategoryId(categoryId, pageable);
        } else {
            return recipeRepository.findAllWithUserAndCategories(pageable);
        }
    }

    public Recipe assignCategories(Long id, AssignCategoriesRequest request) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found with id: " + id));

        Set<Category> categories = new HashSet<>(categoryRepository.findAllById(request.categoryIds())); // Convert List to Set
        if (categories.size() != request.categoryIds().size()) {
            throw new IllegalArgumentException("One or more category IDs do not exist");
        }

        recipe.setCategories(categories); // Now compatible with Set<Category>
        return recipeRepository.save(recipe);
    }

    public Recipe getRecipeById(Long id) {
        return recipeRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found with id: " + id));
    }

    public Recipe editRecipe(User currentUser, Long id, EditRecipeRequest request) {
        Recipe recipe = recipeRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found with id: " + id));

        if (!recipe.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Only the creator can edit this recipe");
        }

        if (request.name() != null) {
            recipe.setName(request.name());
        }
        if (request.description() != null) {
            recipe.setDescription(request.description());
        }
        if (request.portions() != null) {
            recipe.setPortions(request.portions());
        }
        if (request.imgUrl() != null) {
            recipe.setImgUrl(request.imgUrl());
        }
        if (request.categoryIds() != null) {
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(request.categoryIds()));
            if (categories.size() != request.categoryIds().size()) {
                throw new IllegalArgumentException("One or more category IDs do not exist");
            }
            recipe.setCategories(categories);
        }
        if (request.ingredientIds() != null) {
            Set<Ingredient> ingredients = new HashSet<>(ingredientRepository.findAllById(request.ingredientIds()));
            if (ingredients.size() != request.ingredientIds().size()) {
                throw new IllegalArgumentException("One or more ingredient IDs do not exist");
            }
            recipe.setIngredients(ingredients);
        }

        return recipeRepository.save(recipe);
    }
}