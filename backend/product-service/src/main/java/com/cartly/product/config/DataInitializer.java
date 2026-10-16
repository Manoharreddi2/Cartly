package com.cartly.product.config;

import com.cartly.product.entity.Product;
import com.cartly.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            List<Product> products = List.of(
                    Product.builder()
                            .name("Apple iPhone 17")
                            .description("The most powerful iPhone ever with A19 Pro bionic chip, advanced titanium chassis, and groundbreaking pro camera system with 10x optical zoom.")
                            .price(new BigDecimal("1199.00"))
                            .discount(10)
                            .category("Mobiles")
                            .brand("Apple")
                            .stock(50)
                            .imageUrl("https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=600")
                            .rating(4.9)
                            .reviewCount(142)
                            .isFeatured(true)
                            .build(),

                    Product.builder()
                            .name("Samsung Galaxy S26")
                            .description("Next-generation Galaxy AI smartphone featuring a stunning Dynamic AMOLED 2X display, 200MP sensor, and all-day intelligent battery.")
                            .price(new BigDecimal("1099.00"))
                            .discount(12)
                            .category("Mobiles")
                            .brand("Samsung")
                            .stock(45)
                            .imageUrl("https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=600")
                            .rating(4.8)
                            .reviewCount(98)
                            .isFeatured(true)
                            .build(),

                    Product.builder()
                            .name("Google Pixel 10")
                            .description("Pure Google experience powered by the Tensor G5 chip with unmatched computational photography and Magic Editor AI tools.")
                            .price(new BigDecimal("899.00"))
                            .discount(15)
                            .category("Mobiles")
                            .brand("Google")
                            .stock(30)
                            .imageUrl("https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=600")
                            .rating(4.7)
                            .reviewCount(64)
                            .isFeatured(true)
                            .build(),

                    Product.builder()
                            .name("Sony WH-1000XM6")
                            .description("Industry-leading active noise cancellation wireless headphones with spatial 360 audio, 40-hour battery life, and crystal-clear call quality.")
                            .price(new BigDecimal("399.00"))
                            .discount(20)
                            .category("Electronics")
                            .brand("Sony")
                            .stock(75)
                            .imageUrl("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600")
                            .rating(4.9)
                            .reviewCount(210)
                            .isFeatured(true)
                            .build(),

                    Product.builder()
                            .name("Dell Inspiron Laptop")
                            .description("Versatile 16-inch workhorse laptop with Intel Core Ultra 7 processor, 32GB RAM, 1TB SSD, and anti-glare FHD+ display.")
                            .price(new BigDecimal("949.00"))
                            .discount(8)
                            .category("Laptops")
                            .brand("Dell")
                            .stock(25)
                            .imageUrl("https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=600")
                            .rating(4.6)
                            .reviewCount(82)
                            .isFeatured(true)
                            .build(),

                    Product.builder()
                            .name("Nike Air Max")
                            .description("Iconic street and running sneakers with pressurized air cushioning technology, breathable mesh upper, and timeless urban silhouette.")
                            .price(new BigDecimal("160.00"))
                            .discount(15)
                            .category("Shoes")
                            .brand("Nike")
                            .stock(120)
                            .imageUrl("https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600")
                            .rating(4.8)
                            .reviewCount(350)
                            .isFeatured(true)
                            .build(),

                    Product.builder()
                            .name("Adidas Running Shoes")
                            .description("High-performance lightweight road running shoes engineered with responsive Boost midsole and Continental rubber outsole.")
                            .price(new BigDecimal("140.00"))
                            .discount(10)
                            .category("Shoes")
                            .brand("Adidas")
                            .stock(90)
                            .imageUrl("https://images.unsplash.com/photo-1587563871167-1ee9c731aefb?w=600")
                            .rating(4.7)
                            .reviewCount(175)
                            .isFeatured(false)
                            .build(),

                    Product.builder()
                            .name("Canon Camera")
                            .description("Full-frame mirrorless digital camera with 45MP sensor, 8K RAW video recording, and rapid autofocus tracking for professional creators.")
                            .price(new BigDecimal("2499.00"))
                            .discount(5)
                            .category("Electronics")
                            .brand("Canon")
                            .stock(18)
                            .imageUrl("https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=600")
                            .rating(4.9)
                            .reviewCount(53)
                            .isFeatured(true)
                            .build(),

                    Product.builder()
                            .name("Levi's Vintage Denim Jacket")
                            .description("Classic trucker jacket crafted from 100% heavyweight cotton denim with timeless point collar and welt side pockets.")
                            .price(new BigDecimal("118.00"))
                            .discount(25)
                            .category("Fashion")
                            .brand("Levi's")
                            .stock(60)
                            .imageUrl("https://images.unsplash.com/photo-1576995853123-5a10305d93c0?w=600")
                            .rating(4.6)
                            .reviewCount(89)
                            .isFeatured(false)
                            .build(),

                    Product.builder()
                            .name("Apple Watch Ultra 2")
                            .description("Rugged titanium smartwatch built for outdoor adventure and endurance sports with up to 72 hours in low-power mode.")
                            .price(new BigDecimal("799.00"))
                            .discount(5)
                            .category("Accessories")
                            .brand("Apple")
                            .stock(35)
                            .imageUrl("https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600")
                            .rating(4.9)
                            .reviewCount(120)
                            .isFeatured(true)
                            .build(),

                    Product.builder()
                            .name("Ray-Ban Classic Aviator Sunglasses")
                            .description("Legendary teardrop-shaped metal frame with 100% UV protection polarized crystal lenses for timeless style.")
                            .price(new BigDecimal("185.00"))
                            .discount(10)
                            .category("Accessories")
                            .brand("Ray-Ban")
                            .stock(40)
                            .imageUrl("https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=600")
                            .rating(4.8)
                            .reviewCount(95)
                            .isFeatured(false)
                            .build(),

                    Product.builder()
                            .name("Dyson V15 Cordless Vacuum")
                            .description("Intelligent cordless stick vacuum with laser illumination that reveals invisible dust and powerful cyclone suction.")
                            .price(new BigDecimal("699.00"))
                            .discount(15)
                            .category("Home")
                            .brand("Dyson")
                            .stock(20)
                            .imageUrl("https://images.unsplash.com/photo-1558317374-067fb5f30001?w=600")
                            .rating(4.7)
                            .reviewCount(68)
                            .isFeatured(false)
                            .build(),

                    Product.builder()
                            .name("Nespresso Vertuo Coffee Maker")
                            .description("Precision espresso and brewed coffee maker utilizing Centrifusion technology for naturally formed crema.")
                            .price(new BigDecimal("199.00"))
                            .discount(20)
                            .category("Home")
                            .brand("Nespresso")
                            .stock(50)
                            .imageUrl("https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?w=600")
                            .rating(4.6)
                            .reviewCount(144)
                            .isFeatured(false)
                            .build(),

                    Product.builder()
                            .name("Advanced Repair Night Serum")
                            .description("Multi-recovery synchronized anti-aging night serum enriched with hyaluronic acid for deep hydration and radiance.")
                            .price(new BigDecimal("115.00"))
                            .discount(10)
                            .category("Beauty")
                            .brand("Estée Lauder")
                            .stock(80)
                            .imageUrl("https://images.unsplash.com/photo-1608248597359-bbad2d55d72f?w=600")
                            .rating(4.8)
                            .reviewCount(212)
                            .isFeatured(false)
                            .build(),

                    Product.builder()
                            .name("Tom Ford Noir Luxury Perfume")
                            .description("Sensual amber and wood oriental eau de parfum capturing a mysterious, sophisticated evening presence.")
                            .price(new BigDecimal("210.00"))
                            .discount(0)
                            .category("Beauty")
                            .brand("Tom Ford")
                            .stock(28)
                            .imageUrl("https://images.unsplash.com/photo-1594035910387-fea47794261f?w=600")
                            .rating(4.9)
                            .reviewCount(78)
                            .isFeatured(false)
                            .build(),

                    Product.builder()
                            .name("MacBook Pro 16 M3 Max")
                            .description("Ultimate pro laptop with 16-core CPU, 40-core GPU, Liquid Retina XDR display, and 22-hour battery life.")
                            .price(new BigDecimal("3499.00"))
                            .discount(5)
                            .category("Laptops")
                            .brand("Apple")
                            .stock(15)
                            .imageUrl("https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600")
                            .rating(4.9)
                            .reviewCount(165)
                            .isFeatured(true)
                            .build()
            );

            productRepository.saveAll(products);
            log.info("Successfully seeded {} diverse products for Cartly catalog", products.size());
        }
    }
}
