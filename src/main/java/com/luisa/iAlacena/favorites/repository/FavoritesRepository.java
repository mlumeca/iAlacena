package com.luisa.iAlacena.favorites.repository;

import com.luisa.iAlacena.favorites.model.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoritesRepository extends JpaRepository<Favorites, Long> {
}