package com.luisa.iAlacena.category.repository;

import com.luisa.iAlacena.category.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("""
            SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
            FROM Category c
            WHERE c.name = :name
            """)
    boolean existsByName(@Param("name") String name);

    @Query("""
            SELECT c
            FROM Category c
            WHERE c.name = :name
            """)
    Optional<Category> findByName(@Param("name") String name);

    @Query("""
            SELECT c
            FROM Category c
            WHERE c.id = :id
            """)
    Optional<Category> findById(@Param("id") Long id);

    @Query("""
            SELECT c
            FROM Category c
            """)
    Page<Category> findAll(Pageable pageable);

    @Query("""
            SELECT c
            FROM Category c
            LEFT JOIN FETCH c.childCategories
            WHERE c.id = :id
            """)
    Optional<Category> findByIdWithChildren(@Param("id") Long id);
}