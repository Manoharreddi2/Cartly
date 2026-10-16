package com.cartly.product.service;

import com.cartly.product.dto.ProductPageResponse;
import com.cartly.product.dto.ProductRequest;
import com.cartly.product.dto.ProductResponse;
import com.cartly.product.dto.ProductStatsResponse;
import com.cartly.product.entity.Product;
import com.cartly.product.exception.ResourceNotFoundException;
import com.cartly.product.repository.ProductRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductPageResponse getProducts(String keyword,
                                           String category,
                                           String brand,
                                           BigDecimal minPrice,
                                           BigDecimal maxPrice,
                                           Double minRating,
                                           String sortBy,
                                           int page,
                                           int size) {

        Sort sort = switch (sortBy != null ? sortBy.toLowerCase() : "newest") {
            case "price_asc" -> Sort.by("price").ascending();
            case "price_desc" -> Sort.by("price").descending();
            case "rating" -> Sort.by("rating").descending();
            default -> Sort.by("createdAt").descending();
        };

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                Predicate nameMatch = cb.like(cb.lower(root.get("name")), pattern);
                Predicate descMatch = cb.like(cb.lower(root.get("description")), pattern);
                Predicate brandMatch = cb.like(cb.lower(root.get("brand")), pattern);
                predicates.add(cb.or(nameMatch, descMatch, brandMatch));
            }

            if (category != null && !category.isBlank() && !category.equalsIgnoreCase("all")) {
                predicates.add(cb.equal(cb.lower(root.get("category")), category.trim().toLowerCase()));
            }

            if (brand != null && !brand.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("brand")), brand.trim().toLowerCase()));
            }

            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            if (minRating != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), minRating));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        List<ProductResponse> responses = productPage.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ProductPageResponse.builder()
                .content(responses)
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .last(productPage.isLast())
                .build();
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return toResponse(product);
    }

    public List<ProductResponse> getFeaturedProducts() {
        return productRepository.findTop8ByIsFeaturedTrueOrderByRatingDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getNewArrivals() {
        return productRepository.findTop8ByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getBestSellers() {
        return productRepository.findTop8ByOrderByRatingDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .price(request.getPrice())
                .discount(request.getDiscount() != null ? request.getDiscount() : 0)
                .category(request.getCategory().trim())
                .brand(request.getBrand() != null ? request.getBrand().trim() : "Cartly")
                .stock(request.getStock())
                .imageUrl(request.getImageUrl())
                .rating(request.getRating() != null ? request.getRating() : 4.5)
                .reviewCount(1)
                .isFeatured(request.isFeatured())
                .build();

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setName(request.getName().trim());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscount(request.getDiscount() != null ? request.getDiscount() : 0);
        product.setCategory(request.getCategory().trim());
        if (request.getBrand() != null) {
            product.setBrand(request.getBrand().trim());
        }
        product.setStock(request.getStock());
        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }
        if (request.getRating() != null) {
            product.setRating(request.getRating());
        }
        product.setFeatured(request.isFeatured());

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }

    public ProductStatsResponse getStats() {
        long total = productRepository.count();
        long lowStock = productRepository.countByStockLessThanEqual(10);
        return ProductStatsResponse.builder()
                .totalProducts(total)
                .lowStockProducts(lowStock)
                .categoriesCount(8)
                .build();
    }

    public ProductResponse toResponse(Product p) {
        BigDecimal discountedPrice = p.getPrice();
        if (p.getDiscount() != null && p.getDiscount() > 0) {
            BigDecimal discountMultiplier = BigDecimal.valueOf(100 - p.getDiscount())
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            discountedPrice = p.getPrice().multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP);
        }

        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .discount(p.getDiscount())
                .discountedPrice(discountedPrice)
                .category(p.getCategory())
                .brand(p.getBrand())
                .stock(p.getStock())
                .imageUrl(p.getImageUrl())
                .rating(p.getRating())
                .reviewCount(p.getReviewCount())
                .isFeatured(p.isFeatured())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
