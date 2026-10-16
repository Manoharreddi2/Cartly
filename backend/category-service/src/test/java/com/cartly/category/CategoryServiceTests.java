package com.cartly.category;

import com.cartly.category.dto.CategoryRequest;
import com.cartly.category.dto.CategoryResponse;
import com.cartly.category.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CategoryServiceTests {

    @Autowired
    private CategoryService categoryService;

    @Test
    void testGetActiveCategories() {
        List<CategoryResponse> categories = categoryService.getActiveCategories();
        assertNotNull(categories);
        assertTrue(categories.size() >= 8);
    }

    @Test
    void testGetCategoryBySlug() {
        CategoryResponse cat = categoryService.getCategoryBySlug("electronics");
        assertNotNull(cat);
        assertEquals("Electronics", cat.getName());
    }

    @Test
    void testCreateCategory() {
        CategoryRequest request = CategoryRequest.builder()
                .name("Sports & Fitness")
                .slug("sports-fitness")
                .description("Gym equipment and outdoor gear")
                .active(true)
                .build();

        CategoryResponse created = categoryService.createCategory(request);
        assertNotNull(created.getId());
        assertEquals("sports-fitness", created.getSlug());
    }
}
