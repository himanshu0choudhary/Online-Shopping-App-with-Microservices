package com.shoppingapp.inventoryservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shoppingapp.inventoryservice.model.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long>{

    Optional<Inventory> findBySkuCode();
    
}
