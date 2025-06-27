package com.foodsphere.service.impl;

import com.foodsphere.model.Category;
import com.foodsphere.model.Food;
import com.foodsphere.model.Restaurant;
import com.foodsphere.repository.CategoryRepository;
import com.foodsphere.repository.FoodRepository;
import com.foodsphere.repository.RestaurantRepository;
import com.foodsphere.request.CreateFoodRequest;
import com.foodsphere.service.FoodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the FoodService interface, providing business logic for managing food items.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {

    private final FoodRepository foodRepository;
    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Creates a new food item based on the provided request object.
     *
     * @param foodRequest the request object containing food item details
     * @param category    the category of the food item
     * @param restaurant  the restaurant that owns the food item
     * @return the persisted food item
     */
    @Override
    public Food createFood(CreateFoodRequest foodRequest, Category category, Restaurant restaurant) {
        log.info("Attempting to create food item: {}", foodRequest.getName());

        if (category.getId() == null) {
            category = categoryRepository.save(category);
            log.info("Category saved with ID: {}", category.getId());
        }

        if (restaurant.getId() == null) {
            restaurant = restaurantRepository.save(restaurant);
            log.info("Restaurant saved with ID: {}", restaurant.getId());
        }

        Food food = new Food();
        food.setFoodCategory(category);
        food.setRestaurant(restaurant);
        food.setName(foodRequest.getName());
        food.setDescription(foodRequest.getDescription());
        food.setPrice(foodRequest.getPrice());
        food.setImages(foodRequest.getImages());
        food.setIngredients(foodRequest.getIngredients());
        food.setSeasonal(foodRequest.isSeasonal());
        food.setVegetarian(foodRequest.isVegetarian());

        Food persistedFood = foodRepository.save(food);
        restaurant.getFoods().add(persistedFood);

        log.info("Food item created with ID: {}", persistedFood.getId());
        return persistedFood;
    }

    /**
     * Deletes a food item by its ID.
     *
     * @param foodId the ID of the food item to delete
     * @throws Exception if the food item is not found
     */
    @Override
    public void deleteFood(Long foodId) throws Exception {
        log.info("Attempting to delete food item with ID: {}", foodId);
        Food food = findFoodById(foodId);
        food.setRestaurant(null);
        foodRepository.delete(food);
        log.info("Food item deleted with ID: {}", foodId);
    }

    /**
     * Retrieves all food items for a given restaurant ID.
     *
     * @param restaurantId the ID of the restaurant
     * @param isVegetarian whether to filter by vegetarian food items
     * @param isNonVeg     whether to filter by non-vegetarian food items
     * @param isSeasonal   whether to filter by seasonal food items
     * @param foodCategory the category of food items to filter by
     * @return a list of food items
     */
    @Override
    public List<Food> getRestaurantsFood(Long restaurantId, boolean isVegetarian, boolean isNonVeg,
                                         boolean isSeasonal, String foodCategory) {
        log.info("Fetching food items for restaurant ID: {}", restaurantId);
        List<Food> foods = foodRepository.findByRestaurantId(restaurantId);

        if (isVegetarian) {
            foods = filterByVegetarian(foods, isVegetarian);
        }
        if (isNonVeg) {
            foods = filterByNonVeg(foods, isNonVeg);
        }
        if (isSeasonal) {
            foods = filterBySeasonal(foods, isSeasonal);
        }
        if (foodCategory != null && !foodCategory.isEmpty()) {
            foods = filterByCategory(foods, foodCategory);
        }

        log.info("Found {} food items for restaurant ID: {}", foods.size(), restaurantId);
        return foods;
    }

    /**
     * Filters a list of food items by a given category.
     *
     * @param foods       the list of food items to filter
     * @param foodCategory the category to filter by
     * @return a list of food items filtered by category
     */
    private List<Food> filterByCategory(List<Food> foods, String foodCategory) {
        return foods.stream()
                .filter(food -> food.getFoodCategory() != null && food.getFoodCategory().getName().equals(foodCategory))
                .collect(Collectors.toList());
    }

    /**
     * Filters a list of food items by non-vegetarian food items.
     *
     * @param foods the list of food items to filter
     * @param isNonVeg whether to filter by non-vegetarian food items
     * @return a list of food items filtered by non-vegetarian
     */
    private List<Food> filterByNonVeg(List<Food> foods, boolean isNonVeg) {
        return foods.stream()
                .filter(food -> food.isVegetarian() != isNonVeg)
                .collect(Collectors.toList());
    }

    /**
     * Filters a list of food items by seasonal food items.
     *
     * @param foods the list of food items to filter
     * @param isSeasonal whether to filter by seasonal food items
     * @return a list of food items filtered by seasonal
     */
    private List<Food> filterBySeasonal(List<Food> foods, boolean isSeasonal) {
        return foods.stream()
                .filter(food -> food.isSeasonal() == isSeasonal)
                .collect(Collectors.toList());
    }

    /**
     * Filters a list of food items by vegetarian food items.
     *
     * @param foods the list of food items to filter
     * @param isVegetarian whether to filter by vegetarian food items
     * @return a list of food items filtered by vegetarian
     */
    private List<Food> filterByVegetarian(List<Food> foods, boolean isVegetarian) {
        return foods.stream()
                .filter(food -> food.isVegetarian() == isVegetarian)
                .collect(Collectors.toList());
    }

    /**
     * Searches for food items by a given keyword.
     *
     * @param keyword the keyword to search by
     * @return a list of food items matching the search keyword
     */
    @Override
    public List<Food> searchFood(String keyword) {
        log.info("Searching for food items with keyword: {}", keyword);
        return foodRepository.searchFood(keyword);
    }

    /**
     * Finds a food item by its ID.
     *
     * @param foodId the ID of the food item to find
     * @return the found food item
     * @throws Exception if the food item is not found
     */
    @Override
    public Food findFoodById(Long foodId) throws Exception {
        log.info("Searching for food item with ID: {}", foodId);
        Optional<Food> optionalFood = foodRepository.findById(foodId);
        if (optionalFood.isEmpty()) {
            log.error("Food item not found with ID: {}", foodId);
            throw new Exception("Food not found with ID: " + foodId);
        }
        return optionalFood.get();
    }

    /**
     * Updates the availability status of a food item.
     *
     * @param foodId the ID of the food item to update
     * @return the updated food item
     * @throws Exception if the food item is not found
     */
    @Override
    public Food updateAvailabilityStatus(Long foodId) throws Exception {
        log.info("Updating availability status for food item with ID: {}", foodId);
        Food food = findFoodById(foodId);
        food.setAvailable(!food.isAvailable());
        Food updatedFood = foodRepository.save(food);
        log.info("Availability status updated for food item with ID: {}", foodId);
        return updatedFood;
    }
}