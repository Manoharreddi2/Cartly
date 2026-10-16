package com.cartly.category.config;

import com.cartly.category.entity.Category;
import com.cartly.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() == 0) {
            List<Category> categories = List.of(
                    Category.builder()
                            .name("Electronics")
                            .slug("electronics")
                            .description("Cutting-edge gadgets, audio devices, smart tech and premium electronics")
                            .imageUrl("https://images.unsplash.com/photo-1498049794561-7780e7231661?w=400")
                            .active(true)
                            .build(),
                    Category.builder()
                            .name("Mobiles")
                            .slug("mobiles")
                            .description("Latest 5G smartphones, flagship devices, and mobile gear")
                            .imageUrl("https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400")
                            .active(true)
                            .build(),
                    Category.builder()
                            .name("Laptops")
                            .slug("laptops")
                            .description("High-performance work laptops, ultrabooks, and gaming powerhouses")
                            .imageUrl("https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400")
                            .active(true)
                            .build(),
                    Category.builder()
                            .name("Fashion")
                            .slug("fashion")
                            .description("Trending apparel, seasonal collections, jackets and stylish clothing")
                            .imageUrl("https://images.unsplash.com/photo-1445205170230-053b83016050?w=400")
                            .active(true)
                            .build(),
                    Category.builder()
                            .name("Shoes")
                            .slug("shoes")
                            .description("Running sneakers, casual trainers, athletic footwear, and formal shoes")
                            .imageUrl("https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400")
                            .active(true)
                            .build(),
                    Category.builder()
                            .name("Accessories")
                            .slug("accessories")
                            .description("Smartwatches, headphones, luxury wallets, sunglasses and travel gear")
                            .imageUrl("https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400")
                            .active(true)
                            .build(),
                    Category.builder()
                            .name("Home")
                            .slug("home")
                            .description("Smart home appliances, modern decor, kitchen essentials and furniture")
                            .imageUrl("https://images.unsplash.com/photo-1513694203232-719a280e022f?w=400")
                            .active(true)
                            .build(),
                    Category.builder()
                            .name("Beauty")
                            .slug("beauty")
                            .description("Skincare serums, luxury fragrances, cosmetics and organic grooming")
                            .imageUrl("https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=400")
                            .active(true)
                            .build()
            );

            categoryRepository.saveAll(categories);
            log.info("Successfully seeded {} default categories for Cartly", categories.size());
        }
    }
}
