package com.luisa.iAlacena.inventory.service;

import com.luisa.iAlacena.ingredient.model.Ingredient;
import com.luisa.iAlacena.ingredient.repository.IngredientRepository;
import com.luisa.iAlacena.inventory.dto.AddInventoryRequest;
import com.luisa.iAlacena.inventory.dto.InventoryResponse;
import com.luisa.iAlacena.inventory.dto.UpdateInventoryRequest;
import com.luisa.iAlacena.inventory.model.Inventory;
import com.luisa.iAlacena.inventory.repository.InventoryRepository;
import com.luisa.iAlacena.user.model.User;
import com.luisa.iAlacena.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;

    public InventoryService(InventoryRepository inventoryRepository, UserRepository userRepository, IngredientRepository ingredientRepository) {
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @Transactional
    public InventoryResponse addIngredientToInventory(UUID userId, AddInventoryRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Ingredient ingredient = ingredientRepository.findById(request.ingredientId())
                .orElseThrow(() -> new IllegalArgumentException("Ingredient not found with id: " + request.ingredientId()));

        Inventory inventory = Inventory.builder()
                .user(user)
                .ingredient(ingredient)
                .quantity(request.quantity())
                .build();

        Inventory savedInventory = inventoryRepository.save(inventory);
        return InventoryResponse.of(savedInventory);
    }

    @Transactional
    public Page<InventoryResponse> getAllInventoryItems(UUID userId, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Page<Inventory> inventoryPage = inventoryRepository.findByUserId(userId, pageable);
        return inventoryPage.map(InventoryResponse::of);
    }

    @Transactional
    public InventoryResponse updateIngredientQuantity(UUID userId, Long ingredientId, UpdateInventoryRequest request) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Inventory inventory = inventoryRepository.findByUserIdAndIngredientId(userId, ingredientId)
                .orElseThrow(() -> new IllegalArgumentException("Ingredient " + ingredientId + " not found in inventory for user " + userId));

        inventory.setQuantity(request.quantity());
        Inventory updatedInventory = inventoryRepository.save(inventory);
        return InventoryResponse.of(updatedInventory);
    }

    @Transactional
    public void deleteIngredientFromInventory(UUID userId, Long ingredientId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Inventory inventory = inventoryRepository.findByUserIdAndIngredientId(userId, ingredientId)
                .orElseThrow(() -> new IllegalArgumentException("Ingredient " + ingredientId + " not found in inventory for user " + userId));

        inventoryRepository.delete(inventory);
    }
}