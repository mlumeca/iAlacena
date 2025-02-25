package com.luisa.iAlacena.category.service;

import com.luisa.iAlacena.category.dto.AssignCategoriesRequest;
import com.luisa.iAlacena.category.dto.CreateCategoryRequest;
import com.luisa.iAlacena.category.dto.CreateSubcategoryRequest;
import com.luisa.iAlacena.category.dto.EditCategoryRequest;
import com.luisa.iAlacena.category.model.Category;
import com.luisa.iAlacena.category.repository.CategoryRepository;
import com.luisa.iAlacena.ingredient.model.Ingredient;
import com.luisa.iAlacena.ingredient.repository.IngredientRepository;
import com.luisa.iAlacena.user.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;

    public CategoryService(CategoryRepository categoryRepository, IngredientRepository ingredientRepository) {
        this.categoryRepository = categoryRepository;
        this.ingredientRepository = ingredientRepository;
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
}