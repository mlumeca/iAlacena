package com.luisa.iAlacena.recipe.service;

import com.luisa.iAlacena.category.dto.AssignCategoriesRequest;
import com.luisa.iAlacena.category.model.Category;
import com.luisa.iAlacena.category.repository.CategoryRepository;
import com.luisa.iAlacena.recipe.dto.CreateRecipeRequest;
import com.luisa.iAlacena.recipe.model.Recipe;
import com.luisa.iAlacena.recipe.repository.RecipeRepository;
import com.luisa.iAlacena.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final CategoryRepository categoryRepository;

    public RecipeService(RecipeRepository recipeRepository, CategoryRepository categoryRepository) {
        this.recipeRepository = recipeRepository;
        this.categoryRepository = categoryRepository;
    }

    public Recipe createRecipe(User currentUser, CreateRecipeRequest request) {
        List<Category> categories = request.categoryIds() != null && !request.categoryIds().isEmpty()
                ? categoryRepository.findAllById(request.categoryIds())
                : List.of();

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

        List<Category> categories = categoryRepository.findAllById(request.categoryIds());
        if (categories.size() != request.categoryIds().size()) {
            throw new IllegalArgumentException("One or more category IDs do not exist");
        }

        recipe.setCategories(categories);
        return recipeRepository.save(recipe);
    }
}