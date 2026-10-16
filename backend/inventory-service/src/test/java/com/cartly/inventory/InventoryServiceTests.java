package com.cartly.inventory;

import com.cartly.inventory.dto.InventoryResponse;
import com.cartly.inventory.exception.InsufficientStockException;
import com.cartly.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class InventoryServiceTests {

    @Autowired
    private InventoryService inventoryService;

    @Test
    void testCheckStock() {
        boolean available = inventoryService.checkStock(Map.of(1L, 2, 2L, 1));
        assertTrue(available);
    }

    @Test
    void testReserveAndDeductStock() {
        Long productId = 1L;
        InventoryResponse initial = inventoryService.getInventory(productId);
        int initialStock = initial.getStockQuantity();

        // Reserve 5
        inventoryService.reserveStock(Map.of(productId, 5));
        InventoryResponse afterReserve = inventoryService.getInventory(productId);
        assertEquals(5, afterReserve.getReservedQuantity());
        assertEquals(initialStock - 5, afterReserve.getAvailableQuantity());

        // Deduct 5
        inventoryService.deductStock(Map.of(productId, 5));
        InventoryResponse afterDeduct = inventoryService.getInventory(productId);
        assertEquals(0, afterDeduct.getReservedQuantity());
        assertEquals(initialStock - 5, afterDeduct.getStockQuantity());
    }

    @Test
    void testReserveExceedingStockThrowsException() {
        assertThrows(InsufficientStockException.class, () ->
                inventoryService.reserveStock(Map.of(1L, 99999)));
    }
}
