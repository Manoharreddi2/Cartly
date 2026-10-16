package com.cartly.inventory.config;

import com.cartly.inventory.entity.InventoryItem;
import com.cartly.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final InventoryRepository inventoryRepository;

    @Override
    public void run(String... args) {
        if (inventoryRepository.count() == 0) {
            List<InventoryItem> items = new ArrayList<>();
            // Product IDs 1 to 16
            int[] stocks = {50, 45, 30, 75, 25, 120, 90, 18, 60, 35, 40, 20, 50, 80, 28, 15};

            for (int i = 0; i < stocks.length; i++) {
                items.add(InventoryItem.builder()
                        .productId((long) (i + 1))
                        .stockQuantity(stocks[i])
                        .reservedQuantity(0)
                        .lowStockThreshold(15)
                        .build());
            }

            inventoryRepository.saveAll(items);
            log.info("Successfully initialized stock records for {} products", items.size());
        }
    }
}
