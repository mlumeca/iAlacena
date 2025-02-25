package com.luisa.iAlacena.ingredient.service;

import com.luisa.iAlacena.category.dto.AssignCategoriesRequest;
import com.luisa.iAlacena.category.model.Category;
import com.luisa.iAlacena.category.repository.CategoryRepository;
import com.luisa.iAlacena.ingredient.dto.CreateIngredientRequest;
import com.luisa.iAlacena.ingredient.model.Ingredient;
import com.luisa.iAlacena.ingredient.repository.IngredientRepository;
import com.luisa.iAlacena.user.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final CategoryRepository categoryRepository;

    public IngredientService(IngredientRepository ingredientRepository, CategoryRepository categoryRepository) {
        this.ingredientRepository = ingredientRepository;
        this.categoryRepository = categoryRepository;
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

        List<Category> categories = categoryRepository.findAllById(request.categoryIds());
        if (categories.size() != request.categoryIds().size()) {
            throw new IllegalArgumentException("One or more category IDs do not exist");
        }

        ingredient.setCategories(categories);
        return ingredientRepository.save(ingredient);
    }
}