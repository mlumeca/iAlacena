package com.luisa.iAlacena.recipe.service;

import com.luisa.iAlacena.recipe.dto.CreateRecipeRequest;
import com.luisa.iAlacena.recipe.model.Recipe;
import com.luisa.iAlacena.recipe.repository.RecipeRepository;
import com.luisa.iAlacena.user.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public Recipe createRecipe(User currentUser, CreateRecipeRequest request) {
        Recipe recipe = Recipe.builder()
                .name(request.name())
                .description(request.description())
                .portions(request.portions())
                //.ingredients(request.ingredients())
                //.categories(calculateCategories(request.ingredients()))
                .user(currentUser)
                .build();
        return recipeRepository.save(recipe);
    }

    /*
    private List<String> calculateCategories(List<String> ingredients) {
        List<String> categories = new ArrayList<>();
        if (ingredients.stream().anyMatch(i -> i.toLowerCase().contains("carne") || i.toLowerCase().contains("pollo"))) {
            categories.add("Carnes");
        }
        if (ingredients.stream().anyMatch(i -> i.toLowerCase().contains("pescado") || i.toLowerCase().contains("marisco"))) {
            categories.add("Pescados");
        }
        if (ingredients.stream().anyMatch(i -> i.toLowerCase().contains("arroz") || i.toLowerCase().contains("pasta"))) {
            categories.add("Carbohidratos");
        }
        if (ingredients.stream().anyMatch(i -> i.toLowerCase().contains("verdura") || i.toLowerCase().contains("fruta"))) {
            categories.add("Vegetariana");
        }
        return categories.isEmpty() ? List.of("General") : categories;
    }

     */
}