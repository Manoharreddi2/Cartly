package com.cartly.inventory.service;

import com.cartly.inventory.dto.InventoryResponse;
import com.cartly.inventory.entity.InventoryItem;
import com.cartly.inventory.exception.InsufficientStockException;
import com.cartly.inventory.exception.ResourceNotFoundException;
import com.cartly.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryResponse getInventory(Long productId) {
        InventoryItem item = inventoryRepository.findByProductId(productId)
                .orElseGet(() -> inventoryRepository.save(InventoryItem.builder()
                        .productId(productId)
                        .stockQuantity(50)
                        .reservedQuantity(0)
                        .lowStockThreshold(10)
                        .build()));
        return toResponse(item);
    }

    public boolean checkStock(Map<Long, Integer> items) {
        for (Map.Entry<Long, Integer> entry : items.entrySet()) {
            Long productId = entry.getKey();
            Integer requestedQty = entry.getValue();

            InventoryItem item = inventoryRepository.findByProductId(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product " + productId + " not found in inventory"));

            if (item.getAvailableQuantity() < requestedQty) {
                return false;
            }
        }
        return true;
    }

    @Transactional
    public void reserveStock(Map<Long, Integer> items) {
        for (Map.Entry<Long, Integer> entry : items.entrySet()) {
            Long productId = entry.getKey();
            Integer qty = entry.getValue();

            InventoryItem item = inventoryRepository.findByProductId(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product " + productId + " not found in inventory"));

            if (item.getAvailableQuantity() < qty) {
                throw new InsufficientStockException("Insufficient stock for product " + productId +
                        ". Available: " + item.getAvailableQuantity() + ", Requested: " + qty);
            }

            item.setReservedQuantity(item.getReservedQuantity() + qty);
            inventoryRepository.save(item);
            log.info("Reserved {} units for product {}. New reserved={}", qty, productId, item.getReservedQuantity());
        }
    }

    @Transactional
    public void deductStock(Map<Long, Integer> items) {
        for (Map.Entry<Long, Integer> entry : items.entrySet()) {
            Long productId = entry.getKey();
            Integer qty = entry.getValue();

            InventoryItem item = inventoryRepository.findByProductId(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product " + productId + " not found in inventory"));

            int newStock = Math.max(0, item.getStockQuantity() - qty);
            int newReserved = Math.max(0, item.getReservedQuantity() - qty);

            item.setStockQuantity(newStock);
            item.setReservedQuantity(newReserved);
            inventoryRepository.save(item);
            log.info("Deducted {} units for product {}. Remaining stock={}", qty, productId, newStock);
        }
    }

    @Transactional
    public void releaseStock(Map<Long, Integer> items) {
        for (Map.Entry<Long, Integer> entry : items.entrySet()) {
            Long productId = entry.getKey();
            Integer qty = entry.getValue();

            InventoryItem item = inventoryRepository.findByProductId(productId).orElse(null);
            if (item != null) {
                item.setReservedQuantity(Math.max(0, item.getReservedQuantity() - qty));
                inventoryRepository.save(item);
                log.info("Released {} reserved units for product {}", qty, productId);
            }
        }
    }

    @Transactional
    public InventoryResponse updateStock(Long productId, int quantity, Integer threshold) {
        InventoryItem item = inventoryRepository.findByProductId(productId)
                .orElseGet(() -> InventoryItem.builder()
                        .productId(productId)
                        .stockQuantity(0)
                        .reservedQuantity(0)
                        .lowStockThreshold(10)
                        .build());

        item.setStockQuantity(quantity);
        if (threshold != null) {
            item.setLowStockThreshold(threshold);
        }
        return toResponse(inventoryRepository.save(item));
    }

    public List<InventoryResponse> getLowStockItems() {
        return inventoryRepository.findLowStockItems().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<InventoryResponse> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private InventoryResponse toResponse(InventoryItem item) {
        int available = item.getAvailableQuantity();
        return InventoryResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .stockQuantity(item.getStockQuantity())
                .reservedQuantity(item.getReservedQuantity())
                .availableQuantity(available)
                .lowStockThreshold(item.getLowStockThreshold())
                .isLowStock(available <= item.getLowStockThreshold())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}
