package com.foodsphere.service.impl;

import com.foodsphere.model.Category;
import com.foodsphere.model.Restaurant;
import com.foodsphere.repository.CategoryRepository;
import com.foodsphere.service.CategoryService;
import com.foodsphere.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    private final RestaurantService restaurantService;
    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(String name, Long userId) throws Exception {
        log.info("Creating category with name: {} for user: {}", name, userId);

        Restaurant restaurant = restaurantService.findRestaurantByUserId(userId);
        Category category = new Category();
        category.setName(name);
        category.setRestaurant(restaurant);

        return categoryRepository.save(category);
    }

    @Override
    public List<Category> findCategoryByRestaurantId(Long restaurantId) throws Exception {
        log.info("Finding categories for restaurant with id: {}", restaurantId);
        Restaurant restaurant = restaurantService.findRestaurantByUserId(restaurantId);

        return categoryRepository.findByRestaurantId(restaurant.getId());
    }

    @Override
    public Category findCategoryById(Long categoryId) {
        log.info("Finding category with id: {}", categoryId);

        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (optionalCategory.isEmpty()) {
            log.error("Category not found with id: {}", categoryId);
            throw new RuntimeException("Category not found with id: " + categoryId);
        }

        return optionalCategory.get();
    }
}