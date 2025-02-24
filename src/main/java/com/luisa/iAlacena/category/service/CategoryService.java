package com.luisa.iAlacena.category.service;

import com.luisa.iAlacena.category.dto.CreateCategoryRequest;
import com.luisa.iAlacena.category.model.Category;
import com.luisa.iAlacena.category.repository.CategoryRepository;
import com.luisa.iAlacena.user.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
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
}
