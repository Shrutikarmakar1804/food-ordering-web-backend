package com.foodsphere.controller;

import com.foodsphere.model.Food;
import com.foodsphere.model.User;
import com.foodsphere.service.FoodService;
import com.foodsphere.service.RestaurantService;
import com.foodsphere.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/food")
public class FoodController {

    private final FoodService foodService;
    private final UserService userService;
    private final RestaurantService restaurantService;

    @GetMapping("/search")
    public ResponseEntity<List<Food>> searchFood(@RequestParam String name, @RequestHeader("Authorization") String jwt) throws Exception {
        log.info("Searching food by name: {}", name);

        User user = userService.findUserByJwt(jwt);
        List<Food> foods = foodService.searchFood(name);

        log.info("Found {} foods", foods.size());
        return new ResponseEntity<>(foods, HttpStatus.OK);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<Food>> getRestaurantFood(@RequestParam boolean vegetarian,
                                                        @RequestParam boolean seasonal,
                                                        @RequestParam boolean nonveg,
                                                        @PathVariable Long restaurantId,
                                                        @RequestParam(required = false) String foodCategory,
                                                        @RequestHeader("Authorization") String jwt) throws Exception {

        log.info("Getting food by restaurant id: {}", restaurantId);

        User user = userService.findUserByJwt(jwt);
        List<Food> foods = foodService.getRestaurantsFood(restaurantId, vegetarian, nonveg, seasonal, foodCategory);

        log.info("Found {} foods", foods.size());
        return new ResponseEntity<>(foods, HttpStatus.OK);
    }

}