package com.cartly.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_product_name", columnList = "name"),
    @Index(name = "idx_product_category", columnList = "category"),
    @Index(name = "idx_product_brand", columnList = "brand"),
    @Index(name = "idx_product_price", columnList = "price")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    @Builder.Default
    private Integer discount = 0; // percentage discount (e.g. 15 for 15%)

    @Column(nullable = false, length = 80)
    private String category;

    @Column(length = 80)
    private String brand;

    @Column(nullable = false)
    @Builder.Default
    private Integer stock = 0;

    @Column(length = 500)
    private String imageUrl;

    @Builder.Default
    private Double rating = 4.5;

    @Builder.Default
    private Integer reviewCount = 0;

    @Builder.Default
    private boolean isFeatured = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
