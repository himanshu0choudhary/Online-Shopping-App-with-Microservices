package com.shoppingapp.inventoryservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shoppingapp.inventoryservice.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    @Transactional
    public boolean isInStock(String skuCode){
        return inventoryRepository.findBySkuCode().isPresent();
    }
}
