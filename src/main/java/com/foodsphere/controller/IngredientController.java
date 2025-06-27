package com.foodsphere.controller;

import com.foodsphere.model.IngredientCategory;
import com.foodsphere.model.IngredientsItem;
import com.foodsphere.request.IngredientCategoryRequest;
import com.foodsphere.request.IngredientRequest;
import com.foodsphere.service.IngredientsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/ingredients")
@Slf4j
public class IngredientController {

    private final IngredientsService ingredientsService;

    /**
     * Creates a new ingredient category for a given restaurant.
     * @param request the request object containing the name and restaurant ID
     * @return the created ingredient category
     * @throws Exception if the restaurant is not found
     */
    @PostMapping("/category/create")
    public ResponseEntity<IngredientCategory> createIngredientCategory(@RequestBody IngredientCategoryRequest request) throws Exception {
        log.info("Creating a new ingredient category for restaurant with ID: {}", request.getRestaurantId());
        IngredientCategory item = ingredientsService.createIngredientCategory(request.getName(), request.getRestaurantId());

        return new ResponseEntity<>(item, HttpStatus.CREATED);
    }

    /**
     * Creates a new ingredients item for a given restaurant and category.
     * @param request the request object containing the name, restaurant ID and category ID
     * @return the created ingredients item
     * @throws Exception if the restaurant or category is not found
     */
    @PostMapping("/items/create")
    public ResponseEntity<IngredientsItem> createIngredientItem(@RequestBody IngredientRequest request) throws Exception {
        log.info("Creating a new ingredients item for restaurant with ID: {} and category with ID: {}", request.getRestaurantId(), request.getCategoryId());
        IngredientsItem item = ingredientsService.createIngredientsItem(request.getRestaurantId(), request.getName(), request.getCategoryId());

        return new ResponseEntity<>(item, HttpStatus.CREATED);
    }

    /**
     * Toggles the stock status of an ingredient item.
     * @param id the ID of the ingredient item
     * @return the updated ingredients item
     * @throws Exception if the ingredient item is not found
     */
    @PutMapping("/stock/{id}")
    public ResponseEntity<IngredientsItem> updateIngredientStock(@PathVariable Long id) throws Exception {
        log.info("Updating the stock status of ingredients item with ID: {}", id);
        IngredientsItem item = ingredientsService.updateStock(id);

        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    /**
     * Finds all ingredients items for a given restaurant ID.
     * @param id the ID of the restaurant
     * @return a list of ingredient items
     * @throws Exception if the restaurant is not found
     */
    @GetMapping("/restaurant/{id}")
    public ResponseEntity<List<IngredientsItem>> getRestaurantIngredient(@PathVariable Long id) throws Exception {
        log.info("Finding all ingredients items for restaurant with ID: {}", id);
        List<IngredientsItem> items = ingredientsService.findRestaurantIngredients(id);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    /**
     * Finds ingredient categories for a given restaurant ID.
     * @param restaurantId the ID of the restaurant
     * @return a list of ingredient categories
     * @throws Exception if the restaurant is not found
     */
    @GetMapping("/restaurant/{restaurantId}/category")
    public ResponseEntity<List<IngredientCategory>> getRestaurantIngredientCategory(@PathVariable Long restaurantId) throws Exception {
        log.info("Finding all ingredient categories for restaurant with ID: {}", restaurantId);
        List<IngredientCategory> items = ingredientsService.findIngredientCategoryByRestaurantId(restaurantId);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

}