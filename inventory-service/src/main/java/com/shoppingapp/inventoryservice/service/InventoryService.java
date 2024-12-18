package com.shoppingapp.inventoryservice.service;

import com.shoppingapp.inventoryservice.dto.InventoryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shoppingapp.inventoryservice.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCodes) {
        return inventoryRepository.findBySkuCodeIn(skuCodes).stream()
                .map(inventory -> InventoryResponse.builder().skuCode(inventory.getSkuCode())
                        .isInStock(inventory.getQuantity() > 0)
                        .build())
                .toList();
    }
}
