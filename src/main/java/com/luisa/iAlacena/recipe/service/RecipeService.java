package com.luisa.iAlacena.recipe.service;

import com.luisa.iAlacena.category.dto.AssignCategoriesRequest;
import com.luisa.iAlacena.category.model.Category;
import com.luisa.iAlacena.category.repository.CategoryRepository;
import com.luisa.iAlacena.favorites.repository.FavoritesRepository;
import com.luisa.iAlacena.ingredient.model.Ingredient;
import com.luisa.iAlacena.ingredient.repository.IngredientRepository;
import com.luisa.iAlacena.recipe.dto.CreateRecipeRequest;
import com.luisa.iAlacena.recipe.dto.EditRecipeRequest;
import com.luisa.iAlacena.recipe.dto.RecipeResponse;
import com.luisa.iAlacena.recipe.model.Recipe;
import com.luisa.iAlacena.recipe.repository.RecipeRepository;
import com.luisa.iAlacena.user.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;
    private final FavoritesRepository favoritesRepository;

    public RecipeService(RecipeRepository recipeRepository, CategoryRepository categoryRepository,
                         IngredientRepository ingredientRepository, FavoritesRepository favoritesRepository) {
        this.recipeRepository = recipeRepository;
        this.categoryRepository = categoryRepository;
        this.ingredientRepository = ingredientRepository;
        this.favoritesRepository = favoritesRepository;
    }

    public RecipeResponse createRecipe(User currentUser, CreateRecipeRequest request) {
        Set<Category> categories = request.categoryIds() != null && !request.categoryIds().isEmpty()
                ? new HashSet<>(categoryRepository.findAllById(request.categoryIds()))
                : Set.of();

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
        Recipe savedRecipe = recipeRepository.save(recipe);
        return RecipeResponse.of(savedRecipe);
    }

    public Page<RecipeResponse> getAllRecipes(User currentUser, Pageable pageable, String name, Long categoryId) {
        if (!currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Only administrators can view all recipes");
        }

        Page<Recipe> recipes;
        if (name != null && !name.isEmpty() && categoryId != null) {
            recipes = recipeRepository.findByNameContainingIgnoreCaseAndCategoryId(name, categoryId, pageable);
        } else if (name != null && !name.isEmpty()) {
            recipes = recipeRepository.findByNameContainingIgnoreCase(name, pageable);
        } else if (categoryId != null) {
            recipes = recipeRepository.findByCategoryId(categoryId, pageable);
        } else {
            recipes = recipeRepository.findAllWithUserAndCategories(pageable);
        }
        return recipes.map(RecipeResponse::of);
    }

    public RecipeResponse assignCategories(Long id, AssignCategoriesRequest request) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found with id: " + id));

        Set<Category> categories = new HashSet<>(categoryRepository.findAllById(request.categoryIds()));
        if (categories.size() != request.categoryIds().size()) {
            throw new IllegalArgumentException("One or more category IDs do not exist");
        }

        recipe.setCategories(categories);
        Recipe updatedRecipe = recipeRepository.save(recipe);
        return RecipeResponse.of(updatedRecipe);
    }

    public RecipeResponse getRecipeById(Long id) {
        Recipe recipe = recipeRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found with id: " + id));
        return RecipeResponse.of(recipe);
    }

    @Transactional
    public RecipeResponse editRecipe(User currentUser, Long id, EditRecipeRequest request) {
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
        if (request.ingredients() != null) {
            Set<Ingredient> ingredients = new HashSet<>(ingredientRepository.findAllById(request.ingredients().keySet()));
            if (ingredients.size() != request.ingredients().size()) {
                throw new IllegalArgumentException("One or more ingredient IDs do not exist");
            }
            recipe.getRecipeIngredients().clear();
            for (Ingredient ingredient : ingredients) {
                int quantity = request.ingredients().getOrDefault(ingredient.getId(), 1);
                recipe.addIngredient(ingredient, quantity);
            }
        }

        Recipe updatedRecipe = recipeRepository.save(recipe);
        return RecipeResponse.of(updatedRecipe);
    }

    @Transactional
    public void deleteRecipe(User currentUser, Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found with id: " + id));
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if (!recipe.getUser().getId().equals(currentUser.getId()) && !isAdmin) {
            throw new AccessDeniedException("Only the creator or an admin can delete this recipe");
        }
        favoritesRepository.deleteByRecipeId(id);
        recipeRepository.delete(recipe);
    }
}