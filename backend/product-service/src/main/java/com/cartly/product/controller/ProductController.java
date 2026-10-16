package com.cartly.product.controller;

import com.cartly.product.dto.*;
import com.cartly.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<ProductPageResponse>> getProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(defaultValue = "newest") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        ProductPageResponse response = productService.getProducts(
                keyword, category, brand, minPrice, maxPrice, minRating, sortBy, page, size);
        return ResponseEntity.ok(ApiResponse.ok("Products retrieved", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.ok("Product retrieved", response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<ProductPageResponse>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        ProductPageResponse response = productService.getProducts(keyword, null, null, null, null, null, "newest", page, size);
        return ResponseEntity.ok(ApiResponse.ok("Search results retrieved", response));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<ProductPageResponse>> getByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        ProductPageResponse response = productService.getProducts(null, category, null, null, null, null, "newest", page, size);
        return ResponseEntity.ok(ApiResponse.ok("Category products retrieved", response));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getFeatured() {
        List<ProductResponse> list = productService.getFeaturedProducts();
        return ResponseEntity.ok(ApiResponse.ok("Featured products retrieved", list));
    }

    @GetMapping("/new-arrivals")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getNewArrivals() {
        List<ProductResponse> list = productService.getNewArrivals();
        return ResponseEntity.ok(ApiResponse.ok("New arrivals retrieved", list));
    }

    @GetMapping("/best-sellers")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getBestSellers() {
        List<ProductResponse> list = productService.getBestSellers();
        return ResponseEntity.ok(ApiResponse.ok("Best sellers retrieved", list));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse created = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Product created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse updated = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Product updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.ok("Product deleted successfully", null));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<ProductStatsResponse>> getStats() {
        ProductStatsResponse stats = productService.getStats();
        return ResponseEntity.ok(ApiResponse.ok("Product statistics retrieved", stats));
    }
}
