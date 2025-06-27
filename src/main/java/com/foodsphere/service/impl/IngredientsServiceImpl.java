package com.foodsphere.service.impl;

import com.foodsphere.model.IngredientCategory;
import com.foodsphere.model.IngredientsItem;
import com.foodsphere.model.Restaurant;
import com.foodsphere.repository.IngredientCategoryRepository;
import com.foodsphere.repository.IngredientsItemRepository;
import com.foodsphere.service.IngredientsService;
import com.foodsphere.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the IngredientsService interface, providing business logic
 * for managing ingredient categories and items.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class IngredientsServiceImpl implements IngredientsService {

    private final IngredientsItemRepository ingredientsItemRepository;
    private final IngredientCategoryRepository ingredientCategoryRepository;
    private final RestaurantService restaurantService;

    /**
     * Creates a new ingredient category for a given restaurant.
     *
     * @param name the name of the ingredient category
     * @param restaurantId the ID of the restaurant
     * @return the created ingredient category
     * @throws Exception if the restaurant is not found
     */
    @Override
    public IngredientCategory createIngredientCategory(String name, Long restaurantId) throws Exception {
        log.info("Attempting to create ingredient category '{}' for restaurant ID: {}", name, restaurantId);
        Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);

        IngredientCategory category = new IngredientCategory();
        category.setRestaurant(restaurant);
        category.setName(name);

        IngredientCategory savedCategory = ingredientCategoryRepository.save(category);
        log.info("Successfully created ingredient category with ID: {}", savedCategory.getId());

        return savedCategory;
    }

    /**
     * Finds an ingredient category by its ID.
     *
     * @param id the ID of the ingredient category
     * @return the found ingredient category
     * @throws Exception if the ingredient category is not found
     */
    @Override
    public IngredientCategory findIngredientCategoryById(Long id) throws Exception {
        log.info("Searching for ingredient category with ID: {}", id);

        Optional<IngredientCategory> optional = ingredientCategoryRepository.findById(id);
        if (optional.isEmpty()) {
            log.error("Ingredient Category with ID: {} not found!", id);
            throw new Exception("Ingredient Category not found!");
        }
        return optional.get();
    }

    /**
     * Finds ingredient categories for a given restaurant ID.
     *
     * @param restaurantId the ID of the restaurant
     * @return a list of ingredient categories
     * @throws Exception if the restaurant is not found
     */
    @Override
    public List<IngredientCategory> findIngredientCategoryByRestaurantId(Long restaurantId) throws Exception {
        log.info("Fetching ingredient categories for restaurant ID: {}", restaurantId);
        restaurantService.findRestaurantById(restaurantId);
        return ingredientCategoryRepository.findByRestaurantId(restaurantId);
    }

    /**
     * Creates a new ingredients item for a given restaurant and category.
     *
     * @param restaurantId the ID of the restaurant
     * @param ingredientName the name of the ingredient item
     * @param categoryId the ID of the ingredient category
     * @return the created ingredients item
     * @throws Exception if the restaurant or category is not found
     */
    @Override
    public IngredientsItem createIngredientsItem(Long restaurantId, String ingredientName, Long categoryId) throws Exception {
        log.info("Creating ingredients item '{}' for restaurant ID: {} and category ID: {}", ingredientName, restaurantId, categoryId);
        Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);
        IngredientCategory category = findIngredientCategoryById(categoryId);

        IngredientsItem item = new IngredientsItem();
        item.setName(ingredientName);
        item.setRestaurant(restaurant);
        item.setCategory(category);

        IngredientsItem savedItem = ingredientsItemRepository.save(item);
        category.getIngredients().add(savedItem);
        log.info("Successfully created ingredients item with ID: {}", savedItem.getId());
        return savedItem;
    }

    /**
     * Finds all ingredients items for a given restaurant ID.
     *
     * @param restaurantId the ID of the restaurant
     * @return a list of ingredient items
     * @throws Exception if the restaurant is not found
     */
    @Override
    public List<IngredientsItem> findRestaurantIngredients(Long restaurantId) throws Exception {
        log.info("Fetching ingredients items for restaurant ID: {}", restaurantId);
        return ingredientsItemRepository.findByRestaurantId(restaurantId);
    }

    /**
     * Toggles the stock status of an ingredient item.
     *
     * @param id the ID of the ingredient item
     * @return the updated ingredients item
     * @throws Exception if the ingredient item is not found
     */
    @Override
    public IngredientsItem updateStock(Long id) throws Exception {
        log.info("Updating stock for ingredients item ID: {}", id);
        Optional<IngredientsItem> optionalIngredientsItem = ingredientsItemRepository.findById(id);
        if (optionalIngredientsItem.isEmpty()) {
            log.error("Ingredients Item with ID: {} not found!", id);
            throw new Exception("Ingredient Item not found!");
        }

        IngredientsItem ingredientsItem = optionalIngredientsItem.get();
        ingredientsItem.setStock(!ingredientsItem.isStock());
        IngredientsItem updatedItem = ingredientsItemRepository.save(ingredientsItem);
        log.info("Successfully updated stock for ingredients item ID: {}", updatedItem.getId());

        return updatedItem;
    }
}