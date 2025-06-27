package com.foodsphere.controller;

import com.foodsphere.model.Food;
import com.foodsphere.model.Restaurant;
import com.foodsphere.model.User;
import com.foodsphere.request.CreateFoodRequest;
import com.foodsphere.response.MessageResponse;
import com.foodsphere.service.FoodService;
import com.foodsphere.service.RestaurantService;
import com.foodsphere.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Handles requests related to foods as an admin.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/foods")
public class AdminFoodController {

    private final FoodService foodService;
    private final UserService userService;
    private final RestaurantService restaurantService;

    /**
     * Creates a new food item based on the provided request object.
     * 
     * @param request the request object containing food item details
     * @param jwt     the JWT token to authenticate the user
     * @return the created food item
     * @throws Exception if the user is not found or if the restaurant is not found
     */
    @PostMapping("/create")
    public ResponseEntity<Food> createFood(@RequestHeader("Authorization") String jwt,
                                           @RequestBody CreateFoodRequest request) throws Exception {
        User user = userService.findUserByJwt(jwt);
        log.info("Creating food item for user: {}", user.getFullName());

        Restaurant restaurant = restaurantService.findRestaurantById(request.getRestaurantId());
        log.info("Creating food item for restaurant: {}", restaurant.getName());

        Food food = foodService.createFood(request, request.getCategory(), restaurant);

        log.info("Created food item: {}", food);

        return new ResponseEntity<>(food, HttpStatus.CREATED);
    }

    /**
     * Deletes a food item by its ID.
     * 
     * @param foodId the ID of the food item to delete
     * @param jwt    the JWT token to authenticate the user
     * @return a message indicating the deletion result
     * @throws Exception if the user is not found or if the food item is not found
     */
    @DeleteMapping("/{foodId}")
    public ResponseEntity<MessageResponse> deleteFood( @RequestHeader("Authorization") String jwt,
                                                       @PathVariable Long foodId) throws Exception {
        User user = userService.findUserByJwt(jwt);
        log.info("Deleting food item with ID: {} for user: {}", foodId, user.getFullName());

        Optional<Food> foodOptional = Optional.ofNullable(foodService.findFoodById(foodId));
        if (foodOptional.isPresent()) {
            Food food = foodOptional.get();
            log.info("Deleting food item: {}", food);

            foodService.deleteFood(foodId);

            MessageResponse deleteFoodMessageResponse = new MessageResponse("Food deleted successfully !");

            return new ResponseEntity<>(deleteFoodMessageResponse, HttpStatus.OK);
        } else {
            MessageResponse deleteFoodMessageResponse = new MessageResponse("Food not found !");

            return new ResponseEntity<>(deleteFoodMessageResponse, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Updates the availability status of a food item.
     * 
     * @param foodId the ID of the food item to update the availability status
     * @param jwt    the JWT token to authenticate the user
     * @return the updated food item
     * @throws Exception if the user is not found, or if the food item is not found
     */
    @PutMapping("/{foodId}")
    public ResponseEntity<Food> updateFoodAvailabilityStatus(@RequestHeader("Authorization") String jwt,
                                                             @PathVariable Long foodId) throws Exception {
        User user = userService.findUserByJwt(jwt);
        log.info("Updating food item with ID: {} for user: {}", foodId, user.getFullName());

        Food food = foodService.updateAvailabilityStatus(foodId);

        log.info("Updated food item: {}", food);

        return new ResponseEntity<>(food, HttpStatus.OK);
    }
}