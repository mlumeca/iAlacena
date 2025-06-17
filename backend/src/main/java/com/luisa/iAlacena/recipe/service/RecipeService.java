package com.luisa.iAlacena.recipe.service;

import com.luisa.iAlacena.category.model.Category;
import com.luisa.iAlacena.category.repository.CategoryRepository;
import com.luisa.iAlacena.favorites.repository.FavoritesRepository;
import com.luisa.iAlacena.ingredient.model.Ingredient;
import com.luisa.iAlacena.ingredient.repository.IngredientRepository;
import com.luisa.iAlacena.recipe.dto.CreateRecipeRequest;
import com.luisa.iAlacena.recipe.dto.EditRecipeRequest;
import com.luisa.iAlacena.recipe.model.Recipe;
import com.luisa.iAlacena.recipe.repository.RecipeRepository;
import com.luisa.iAlacena.storage.dto.FileResponse;
import com.luisa.iAlacena.storage.service.StorageService;
import com.luisa.iAlacena.user.model.User;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.HashSet;
import java.util.Set;

@Service
public class RecipeService {

    private static final Logger log = LoggerFactory.getLogger(RecipeService.class);

    private final RecipeRepository recipeRepository;
    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;
    private final FavoritesRepository favoritesRepository;
    private final StorageService storageService; // Add StorageService

    public RecipeService(RecipeRepository recipeRepository, CategoryRepository categoryRepository,
                         IngredientRepository ingredientRepository, FavoritesRepository favoritesRepository,
                         StorageService storageService) {
        this.recipeRepository = recipeRepository;
        this.categoryRepository = categoryRepository;
        this.ingredientRepository = ingredientRepository;
        this.favoritesRepository = favoritesRepository;
        this.storageService = storageService;
    }

    public Recipe getRecipeById(Long id) {
        log.info("Fetching recipe with id: {}", id);
        Recipe recipe = recipeRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found with id: " + id));
        log.debug("Retrieved recipe: {}", recipe.getName());
        return recipe;
    }

    @Transactional
    public Recipe createRecipe(User currentUser, CreateRecipeRequest request) {
        Set<Category> categories = request.categoryIds() != null && !request.categoryIds().isEmpty()
                ? new HashSet<>(categoryRepository.findAllById(request.categoryIds()))
                : Set.of();

        if (request.categoryIds() != null && categories.size() != request.categoryIds().size()) {
            throw new IllegalArgumentException("One or more category IDs do not exist");
        }

        Set<Ingredient> ingredients = request.ingredientIds() != null && !request.ingredientIds().isEmpty()
                ? new HashSet<>(ingredientRepository.findAllById(request.ingredientIds()))
                : Set.of();

        if (request.ingredientIds() != null && ingredients.size() != request.ingredientIds().size()) {
            throw new IllegalArgumentException("One or more ingredient IDs do not exist");
        }

        Recipe recipe = Recipe.builder()
                .name(request.name())
                .description(request.description())
                .portions(request.portions())
                .categories(categories)
                .ingredients(ingredients)
                .user(currentUser)
                .build();

        return recipeRepository.save(recipe);
    }

    @Transactional
    public Recipe updateRecipeImage(User currentUser, Long id, MultipartFile file) {
        Recipe recipe = recipeRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found with id: " + id));

        if (!recipe.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Only the creator can update the recipe image");
        }

        if (file != null && !file.isEmpty()) {
            validateFile(file); // Validate file
            // Delete old image if exists
            String currentImgUrl = recipe.getImgUrl();
            if (currentImgUrl != null && !currentImgUrl.isEmpty()) {
                String filename = currentImgUrl.substring(currentImgUrl.lastIndexOf("/") + 1);
                try {
                    storageService.deleteFile(filename);
                    log.info("Deleted previous recipe image: {}", filename);
                } catch (Exception e) {
                    log.warn("Could not delete previous recipe image: {}", filename, e);
                }
            }
            // Upload new image
            FileResponse fileResponse = uploadFile(file);
            recipe.setImgUrl(fileResponse.uri());
        } else {
            throw new IllegalArgumentException("No file provided");
        }

        return recipeRepository.save(recipe);
    }

    private void validateFile(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Only image files (e.g., JPEG, PNG) are allowed");
            }
            // Enforce a 5MB size limit
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("File size exceeds 5MB limit");
            }
        }
    }

    public Page<Recipe> getAllRecipes(User currentUser, Pageable pageable, String name, Long categoryId) {
        // Existing implementation remains unchanged
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

    @Transactional
    public Recipe editRecipe(User currentUser, Long id, EditRecipeRequest request, MultipartFile file) {
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

        // Handle image update if provided
        if (file != null && !file.isEmpty()) {
            validateFile(file); // Call validateFile here
            // Delete old image if exists
            String currentImgUrl = recipe.getImgUrl();
            if (currentImgUrl != null && !currentImgUrl.isEmpty()) {
                String filename = currentImgUrl.substring(currentImgUrl.lastIndexOf("/") + 1);
                try {
                    storageService.deleteFile(filename);
                    log.info("Deleted previous recipe image: {}", filename);
                } catch (Exception e) {
                    log.warn("Could not delete previous recipe image: {}", filename, e);
                }
            }
            // Upload new image
            FileResponse fileResponse = uploadFile(file);
            recipe.setImgUrl(fileResponse.uri());
        }

        return recipeRepository.save(recipe);
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

        // Delete associated image if exists
        String imgUrl = recipe.getImgUrl();
        if (imgUrl != null && !imgUrl.isEmpty()) {
            String filename = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);
            try {
                storageService.deleteFile(filename);
                log.info("Deleted recipe image: {}", filename);
            } catch (Exception e) {
                log.warn("Could not delete recipe image: {}", filename, e);
            }
        }

        favoritesRepository.deleteByRecipeId(id);
        recipeRepository.delete(recipe);
    }

    private FileResponse uploadFile(MultipartFile file) {
        var fileMetadata = storageService.store(file);
        String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/download/")
                .path(fileMetadata.getId())
                .toUriString();

        fileMetadata.setURL(uri);

        return FileResponse.builder()
                .id(fileMetadata.getId())
                .name(fileMetadata.getFilename())
                .size(file.getSize())
                .type(file.getContentType())
                .uri(uri)
                .build();
    }


    @Transactional
    public void deleteRecipeImage(User currentUser, Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found with id: " + id));
        if (!recipe.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Only the creator can delete the recipe image");
        }

        String imgUrl = recipe.getImgUrl();
        if (imgUrl != null && !imgUrl.isEmpty()) {
            String filename = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);
            try {
                storageService.deleteFile(filename);
                log.info("Deleted recipe image: {}", filename);
            } catch (Exception e) {
                log.warn("Could not delete recipe image: {}", filename, e);
            }
            recipe.setImgUrl(null);
            recipeRepository.save(recipe);
        }
    }
}