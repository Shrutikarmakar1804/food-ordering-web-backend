package com.foodsphere.controller;

import com.foodsphere.dto.RestaurantDto;
import com.foodsphere.model.Restaurant;
import com.foodsphere.model.User;
import com.foodsphere.service.RestaurantService;
import com.foodsphere.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles requests related to restaurants.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurants")
@Slf4j
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final UserService userService;

    /**
     * Searches for restaurants by keyword.
     */
    @GetMapping("/search")
    public ResponseEntity<List<Restaurant>> searchRestaurant(@RequestHeader("Authorization") String jwt,
                                                             @RequestParam String keyword) throws Exception {
        log.info("Searching for restaurants with keyword: {}", keyword);
        User user = userService.findUserByJwt(jwt);
        List<Restaurant> restaurants = restaurantService.searchRestaurants(keyword);

        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }

    /**
     * Retrieves all restaurants.
     */
    @GetMapping()
    public ResponseEntity<List<Restaurant>> getAllRestaurants(@RequestHeader("Authorization") String jwt) throws Exception {
        log.info("Retrieving all restaurants");
        User user = userService.findUserByJwt(jwt);
        List<Restaurant> restaurants = restaurantService.getAllRestaurant();

        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }

    /**
     * Retrieves a restaurant by its ID.
     */
    @GetMapping("/{restaurantId}")
    public ResponseEntity<Restaurant> findRestaurantById(@RequestHeader("Authorization") String jwt,
                                                         @PathVariable Long restaurantId) throws Exception {
        log.info("Retrieving restaurant with ID: {}", restaurantId);
        User user = userService.findUserByJwt(jwt);
        Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);

        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }

    /**
     * Adds or removes a restaurant from a user's favourites.
     */
    @PutMapping("/{restaurantId}/add-favourites")
    public ResponseEntity<RestaurantDto> addToFavourites(@RequestHeader("Authorization") String jwt,
                                                         @PathVariable Long restaurantId) throws Exception {
        log.info("Updating favourites for user: {}", jwt);
        User user = userService.findUserByJwt(jwt);
        RestaurantDto restaurant = restaurantService.addToFavourites(restaurantId, user);

        return new ResponseEntity<>(restaurant, HttpStatus.OK);
    }
}