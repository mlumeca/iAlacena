package com.luisa.iAlacena.shoppingcart.service;

import com.luisa.iAlacena.ingredient.model.Ingredient;
import com.luisa.iAlacena.ingredient.repository.IngredientRepository;
import com.luisa.iAlacena.recipe.model.Recipe;
import com.luisa.iAlacena.recipe.model.RecipeIngredient;
import com.luisa.iAlacena.recipe.repository.RecipeRepository;
import com.luisa.iAlacena.shoppingcart.dto.AddToCartRequest;
import com.luisa.iAlacena.shoppingcart.dto.ShoppingCartResponse;
import com.luisa.iAlacena.shoppingcart.model.ShoppingCart;
import com.luisa.iAlacena.shoppingcart.model.ShoppingCartItem;
import com.luisa.iAlacena.shoppingcart.repository.ShoppingCartItemRepository;
import com.luisa.iAlacena.shoppingcart.repository.ShoppingCartRepository;
import com.luisa.iAlacena.user.model.User;
import com.luisa.iAlacena.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;
    private final ShoppingCartItemRepository shoppingCartItemRepository;
    private final RecipeRepository recipeRepository;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, UserRepository userRepository, IngredientRepository ingredientRepository, ShoppingCartItemRepository shoppingCartItemRepository, RecipeRepository recipeRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.userRepository = userRepository;
        this.ingredientRepository = ingredientRepository;
        this.shoppingCartItemRepository = shoppingCartItemRepository;
        this.recipeRepository = recipeRepository;
    }

    @Transactional
    public ShoppingCartResponse addIngredientToCart(UUID userId, AddToCartRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Ingredient ingredient = ingredientRepository.findById(request.ingredientId())
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found with id: " + request.ingredientId()));

        ShoppingCart cart = user.getShoppingCart();
        if (cart == null) {
            throw new IllegalStateException("Shopping cart not initialized for user " + userId);
        }

        cart.addIngredient(ingredient);
        ShoppingCart updatedCart = shoppingCartRepository.save(cart);

        return ShoppingCartResponse.of(updatedCart);
    }

    @Transactional
    public ShoppingCartResponse removeIngredientFromCart(UUID userId, Long ingredientId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found with id: " + ingredientId));

        ShoppingCart cart = user.getShoppingCart();
        if (cart == null) {
            throw new IllegalStateException("Shopping cart not initialized for user " + userId);
        }
        if (!cart.getItems().containsKey(ingredient)) {
            throw new IllegalArgumentException("Ingredient " + ingredientId + " not found in shopping cart for user " + userId);
        }
        cart.removeIngredient(ingredient);
        ShoppingCart updatedCart = shoppingCartRepository.save(cart);

        return ShoppingCartResponse.of(updatedCart);
    }

    @Transactional(readOnly = true)
    public Page<ShoppingCartResponse> getCartContents(UUID userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        ShoppingCart cart = user.getShoppingCart();
        if (cart == null) {
            throw new IllegalStateException("Shopping cart not initialized for user " + userId);
        }
        List<ShoppingCartResponse> content = List.of(ShoppingCartResponse.of(cart));
        return new PageImpl<>(content, pageable, content.size());
    }

    @Transactional
    public ShoppingCartResponse clearCart(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        ShoppingCart cart = user.getShoppingCart();
        if (cart == null) {
            throw new IllegalStateException("Shopping cart not initialized for user " + userId);
        }
        cart.clearCart();
        ShoppingCart updatedCart = shoppingCartRepository.save(cart);

        return ShoppingCartResponse.of(updatedCart);
    }

    @Transactional
    public ShoppingCartItem updateItemQuantity(UUID userId, Long ingredientId, int quantity) {
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Shopping cart not found for user: " + userId));

        ShoppingCartItem item = shoppingCartItemRepository.findByShoppingCartIdAndIngredientId(cart.getId(), ingredientId)
                .orElseThrow(() -> new IllegalArgumentException("Ingredient " + ingredientId + " not found in the shopping cart"));

        item.setQuantity(quantity);
        return shoppingCartItemRepository.save(item);
    }

    @Transactional
    public ShoppingCartResponse addRecipeIngredientsToCart(UUID userId, Long recipeId) {
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Shopping cart not found for user: " + userId));

        Recipe recipe = recipeRepository.findByIdWithDetails(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found with id: " + recipeId));

        for (RecipeIngredient recipeIngredient : recipe.getRecipeIngredients()) {
            Ingredient ingredient = recipeIngredient.getIngredient();
            int quantityToAdd = recipeIngredient.getQuantity();

            Optional<ShoppingCartItem> existingItem = shoppingCartItemRepository.findByShoppingCartIdAndIngredientId(cart.getId(), ingredient.getId());
            if (existingItem.isPresent()) {
                ShoppingCartItem item = existingItem.get();
                item.setQuantity(item.getQuantity() + quantityToAdd);
                shoppingCartItemRepository.save(item);
            } else {
                ShoppingCartItem newItem = ShoppingCartItem.builder()
                        .shoppingCart(cart)
                        .ingredient(ingredient)
                        .quantity(quantityToAdd)
                        .build();
                shoppingCartItemRepository.save(newItem);
            }
        }

        ShoppingCart updatedCart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Shopping cart not found after update"));
        return ShoppingCartResponse.of(updatedCart);
    }
}