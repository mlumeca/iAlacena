package com.luisa.iAlacena.ingredient.service;

import com.luisa.iAlacena.ingredient.dto.CreateIngredientRequest;
import com.luisa.iAlacena.ingredient.model.Ingredient;
import com.luisa.iAlacena.ingredient.repository.IngredientRepository;
import com.luisa.iAlacena.user.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
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
}