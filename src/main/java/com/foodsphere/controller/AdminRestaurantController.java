package com.foodsphere.controller;

import com.foodsphere.model.Restaurant;
import com.foodsphere.model.User;
import com.foodsphere.request.CreateRestaurantRequest;
import com.foodsphere.response.MessageResponse;
import com.foodsphere.service.RestaurantService;
import com.foodsphere.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/restaurants")
public class AdminRestaurantController {

    private final RestaurantService restaurantService;
    private final UserService userService;

    /**
     * Creates a new restaurant entity.
     *
     * @param createRestaurantRequest the request object containing restaurant details
     * @param jwt               the JWT token to authenticate the user
     * @return the created restaurant
     * @throws Exception if the user is not found or if the restaurant already exists
     */
    @PostMapping()
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody CreateRestaurantRequest createRestaurantRequest,
                                                       @RequestHeader("Authorization") String jwt) throws Exception {
        log.info("Creating a new restaurant");
        User user = userService.findUserByJwt(jwt);
        log.info("Creating a new restaurant for user: {}", user.getFullName());
        Restaurant restaurant = restaurantService.createRestaurant(createRestaurantRequest, user);

        return new ResponseEntity<>(restaurant, CREATED);
    }

    /**
     * Updates an existing restaurant's details.
     *
     * @param request          the request object containing updated details
     * @param jwt              the JWT token to authenticate the user
     * @param restaurantId     the ID of the restaurant to update
     * @return the updated restaurant
     * @throws Exception if the user is not found or if the restaurant is not found
     */
    @PutMapping("/{restaurantId}")
    public ResponseEntity<Restaurant> updateRestaurant(@RequestBody CreateRestaurantRequest request,
                                                       @RequestHeader("Authorization") String jwt,
                                                       @PathVariable Long restaurantId) throws Exception {
        User user = userService.findUserByJwt(jwt);
        log.info("Updating restaurant with ID: {} for user: {}", restaurantId, user.getFullName());
        Restaurant restaurant = restaurantService.updateRestaurant(restaurantId, request);

        return new ResponseEntity<>(restaurant, OK);
    }

    /**
     * Deletes a restaurant by ID.
     *
     * @param jwt              the JWT token to authenticate the user
     * @param restaurantId     the ID of the restaurant to delete
     * @return a message indicating the deletion result
     * @throws Exception if the user is not found or if the restaurant is not found
     */
    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<MessageResponse> deleteRestaurant(@RequestHeader("Authorization") String jwt,
                                                            @PathVariable Long restaurantId) throws Exception {
        User user = userService.findUserByJwt(jwt);
        log.info("Deleting restaurant with ID: {} for user: {}", restaurantId, user.getFullName());
        restaurantService.deleteRestaurant(restaurantId);

        MessageResponse deletionMessage = new MessageResponse("Restaurant deleted successfully !");

        return new ResponseEntity<>(deletionMessage, OK);
    }

    /**
     * Updates the status of a restaurant. Means it Toggles the open status of a restaurant.{open or close}
     *
     * @param jwt              the JWT token to authenticate the user
     * @param restaurantId     the ID of the restaurant to update the status
     * @return the updated restaurant
     * @throws Exception if the user is not found or if the restaurant is not found
     */
    @PutMapping("/{restaurantId}/status")
    public ResponseEntity<Restaurant> updateRestaurantStatus(@RequestHeader("Authorization") String jwt,
                                                             @PathVariable Long restaurantId) throws Exception {

        User user = userService.findUserByJwt(jwt);
        log.info("Updating the status of restaurant with ID: {} for user: {}", restaurantId, user.getFullName());
        Restaurant restaurant = restaurantService.updateRestaurantStatus(restaurantId);

        return new ResponseEntity<>(restaurant, OK);
    }

    /**
     * Finds a restaurant by the owner's user ID.
     *
     * @param jwt              the JWT token to authenticate the user
     * @return the found restaurant
     * @throws Exception if the user is not found or if the restaurant is not found
     */
    @GetMapping("/user")
    public ResponseEntity<Restaurant> findRestaurantByOwnerId(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwt(jwt);
        log.info("Finding restaurant for user: {}", user.getFullName());
        Restaurant restaurant = restaurantService.findRestaurantByUserId(user.getId());

        return new ResponseEntity<>(restaurant, OK);
    }
}