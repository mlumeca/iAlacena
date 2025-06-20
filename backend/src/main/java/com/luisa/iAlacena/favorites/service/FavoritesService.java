package com.luisa.iAlacena.favorites.service;

import com.luisa.iAlacena.favorites.dto.AddFavoriteRequest;
import com.luisa.iAlacena.favorites.dto.FavoritesResponse;
import com.luisa.iAlacena.favorites.model.Favorites;
import com.luisa.iAlacena.favorites.repository.FavoritesRepository;
import com.luisa.iAlacena.recipe.model.Recipe;
import com.luisa.iAlacena.recipe.repository.RecipeRepository;
import com.luisa.iAlacena.user.model.User;
import com.luisa.iAlacena.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FavoritesService {

    private final FavoritesRepository favoritesRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    public FavoritesService(FavoritesRepository favoritesRepository, UserRepository userRepository, RecipeRepository recipeRepository) {
        this.favoritesRepository = favoritesRepository;
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
    }

    @Transactional
    public FavoritesResponse addRecipeToFavorites(UUID userId, AddFavoriteRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Recipe recipe = recipeRepository.findById(request.recipeId())
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found with id: " + request.recipeId()));

        Favorites favorite = Favorites.builder()
                .user(user)
                .recipe(recipe)
                .build();

        Favorites savedFavorite = favoritesRepository.save(favorite);
        return FavoritesResponse.of(savedFavorite);
    }

    @Transactional
    public void removeRecipeFromFavorites(UUID userId, Long recipeId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found with id: " + recipeId));

        Favorites favorite = favoritesRepository.findByUserIdAndRecipeId(userId, recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Favorite entry not found for user " + userId + " and recipe " + recipeId));
        favoritesRepository.delete(favorite);
    }

    @Transactional
    public Page<FavoritesResponse> getAllFavorites(UUID userId, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Page<Favorites> favoritesPage = favoritesRepository.findByUserId(userId, pageable);
        return favoritesPage.map(FavoritesResponse::of);
    }
}