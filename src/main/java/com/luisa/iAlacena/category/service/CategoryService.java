package com.luisa.iAlacena.category.service;

import com.luisa.iAlacena.category.dto.*;
import com.luisa.iAlacena.category.model.Category;
import com.luisa.iAlacena.category.repository.CategoryRepository;
import com.luisa.iAlacena.ingredient.model.Ingredient;
import com.luisa.iAlacena.ingredient.repository.IngredientRepository;
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
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;

    public CategoryService(CategoryRepository categoryRepository, IngredientRepository ingredientRepository, RecipeRepository recipeRepository) {
        this.categoryRepository = categoryRepository;
        this.ingredientRepository = ingredientRepository;
        this.recipeRepository = recipeRepository;
    }

    public Category createCategory(User currentUser, CreateCategoryRequest request) {
        if (!currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Only administrators can create categories");
        }
        if (categoryRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Category with name '" + request.name() + "' already exists");
        }
        Category parentCategory = null;
        if (request.parentCategoryId() != null) {
            parentCategory = categoryRepository.findById(request.parentCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found with id: " + request.parentCategoryId()));
        }
        Category category = Category.builder()
                .name(request.name())
                .parentCategory(parentCategory)
                .build();
        return categoryRepository.save(category);
    }

    public Category editCategory(User currentUser, Long id, EditCategoryRequest request) {
        if (!currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Only administrators can edit categories");
        }
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));
        if (!request.name().equals(category.getName()) && categoryRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Category with name '" + request.name() + "' already exists");
        }
        category.setName(request.name());
        if (request.parentCategoryId() != null) {
            Category newParent = categoryRepository.findById(request.parentCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found with id: " + request.parentCategoryId()));
            category.setParentCategory(newParent);
        } else {
            category.setParentCategory(null);
        }
        return categoryRepository.save(category);
    }

    public Page<Category> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findByIdWithChildren(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));
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

    @Transactional
    public Category createSubcategory(User currentUser, Long parentId, CreateSubcategoryRequest request) {
        if (!currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Only administrators can create subcategories");
        }

        if (categoryRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Category with name '" + request.name() + "' already exists");
        }

        Category parentCategory = categoryRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent category not found with id: " + parentId));

        Category subcategory = Category.builder()
                .name(request.name())
                .parentCategory(parentCategory)
                .build();

        parentCategory.getChildCategories().add(subcategory);

        return categoryRepository.save(subcategory);
    }

    @Transactional
    public Category reassignParentCategory(User currentUser, Long categoryId, ReassignParentRequest request) {
        if (!currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Only administrators can reassign parent categories");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));

        Category newParent = categoryRepository.findById(request.parentId())
                .orElseThrow(() -> new IllegalArgumentException("Parent category not found with id: " + request.parentId()));

        if (createsCycle(category, newParent)) {
            throw new IllegalArgumentException("Reassigning '" + newParent.getName() + "' as parent of '" + category.getName() + "' would create a cycle");
        }

        Category oldParent = category.getParentCategory();
        if (oldParent != null) {
            oldParent.getChildCategories().remove(category);
            categoryRepository.save(oldParent);
        }

        category.setParentCategory(newParent);
        newParent.getChildCategories().add(category);

        return categoryRepository.save(category);
    }
    
    private boolean createsCycle(Category category, Category potentialParent) {
        Category current = potentialParent;
        while (current != null) {
            if (current.equals(category)) {
                return true;
            }
            current = current.getParentCategory();
        }
        return false;
    }

    @Transactional
    public void deleteCategory(User currentUser, Long id) {
        if (!currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Only administrators can delete categories");
        }

        Category category = categoryRepository.findByIdWithChildren(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));

        List<Category> subcategories = category.getChildCategories();
        for (Category subcategory : subcategories) {
            subcategory.setParentCategory(null);
            categoryRepository.save(subcategory);
        }
        category.getChildCategories().clear();

        Category parent = category.getParentCategory();
        if (parent != null) {
            parent.getChildCategories().remove(category);
            categoryRepository.save(parent);
        }

        Set<Category> categoriesToRemove = new HashSet<>();
        categoriesToRemove.add(category);
        List<Ingredient> ingredients = ingredientRepository.findAll();
        for (Ingredient ingredient : ingredients) {
            if (ingredient.getCategories().removeAll(categoriesToRemove)) {
                ingredientRepository.save(ingredient);
            }
        }

        List<Recipe> recipes = recipeRepository.findAll();
        for (Recipe recipe : recipes) {
            if (recipe.getCategories().removeAll(categoriesToRemove)) {
                recipeRepository.save(recipe);
            }
        }

        categoryRepository.delete(category);
    }
}