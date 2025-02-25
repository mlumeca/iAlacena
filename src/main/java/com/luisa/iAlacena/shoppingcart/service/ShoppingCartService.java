package com.luisa.iAlacena.shoppingcart.service;

import com.luisa.iAlacena.ingredient.model.Ingredient;
import com.luisa.iAlacena.ingredient.repository.IngredientRepository;
import com.luisa.iAlacena.shoppingcart.dto.AddToCartRequest;
import com.luisa.iAlacena.shoppingcart.dto.ShoppingCartResponse;
import com.luisa.iAlacena.shoppingcart.model.ShoppingCart;
import com.luisa.iAlacena.shoppingcart.repository.ShoppingCartRepository;
import com.luisa.iAlacena.user.model.User;
import com.luisa.iAlacena.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, UserRepository userRepository, IngredientRepository ingredientRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.userRepository = userRepository;
        this.ingredientRepository = ingredientRepository;
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
}