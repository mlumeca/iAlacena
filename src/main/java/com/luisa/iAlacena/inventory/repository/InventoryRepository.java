package com.luisa.iAlacena.inventory.repository;

import com.luisa.iAlacena.inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}