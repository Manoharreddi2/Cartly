package com.cartly.inventory.controller;

import com.cartly.inventory.dto.*;
import com.cartly.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<InventoryResponse>> getInventory(@PathVariable Long productId) {
        InventoryResponse response = inventoryService.getInventory(productId);
        return ResponseEntity.ok(ApiResponse.ok("Inventory retrieved", response));
    }

    @PostMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> checkStock(@Valid @RequestBody StockCheckRequest request) {
        boolean available = inventoryService.checkStock(request.getItems());
        return ResponseEntity.ok(ApiResponse.ok(available ? "Stock is available" : "Insufficient stock", available));
    }

    @PostMapping("/reserve")
    public ResponseEntity<ApiResponse<Void>> reserveStock(@Valid @RequestBody StockReserveRequest request) {
        inventoryService.reserveStock(request.getItems());
        return ResponseEntity.ok(ApiResponse.ok("Stock reserved successfully", null));
    }

    @PostMapping("/deduct")
    public ResponseEntity<ApiResponse<Void>> deductStock(@Valid @RequestBody StockReserveRequest request) {
        inventoryService.deductStock(request.getItems());
        return ResponseEntity.ok(ApiResponse.ok("Stock deducted successfully", null));
    }

    @PostMapping("/release")
    public ResponseEntity<ApiResponse<Void>> releaseStock(@Valid @RequestBody StockReserveRequest request) {
        inventoryService.releaseStock(request.getItems());
        return ResponseEntity.ok(ApiResponse.ok("Stock released successfully", null));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<InventoryResponse>> updateStock(
            @PathVariable Long productId,
            @RequestBody Map<String, Integer> body) {
        int quantity = body.getOrDefault("quantity", 0);
        Integer threshold = body.get("threshold");
        InventoryResponse updated = inventoryService.updateStock(productId, quantity, threshold);
        return ResponseEntity.ok(ApiResponse.ok("Stock updated successfully", updated));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getLowStock() {
        List<InventoryResponse> lowStock = inventoryService.getLowStockItems();
        return ResponseEntity.ok(ApiResponse.ok("Low stock items retrieved", lowStock));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getAll() {
        List<InventoryResponse> all = inventoryService.getAllInventory();
        return ResponseEntity.ok(ApiResponse.ok("All inventory items retrieved", all));
    }
}
