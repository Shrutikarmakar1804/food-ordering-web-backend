package com.foodsphere.controller;

import com.foodsphere.model.Category;
import com.foodsphere.model.User;
import com.foodsphere.service.CategoryService;
import com.foodsphere.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    /**
     * Creates a new category
     *
     * @param category the category to create
     * @param jwt      the JWT token to authenticate the user
     * @return the created category
     * @throws Exception if the user is not found
     */
    @PostMapping("/admin/category/create")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category,
                                                   @RequestHeader("Authorization") String jwt) throws Exception {
        log.info("Creating a new category with name: {}", category.getName());

        User user = userService.findUserByJwt(jwt);

        Category createdCategory = categoryService.createCategory(category.getName(), user.getId());

        log.info("Created category: {}", createdCategory);

        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    /**
     * Retrieves all categories for a restaurant
     *
     * @param jwt the JWT token to authenticate the user
     * @return the list of categories
     * @throws Exception if the user is not found
     */
    @GetMapping("/category/restaurant")
    public ResponseEntity<List<Category>> findCategoryByRestaurantId(@RequestHeader("Authorization") String jwt) throws Exception {
        log.info("Retrieving all categories for the restaurant");

        User user = userService.findUserByJwt(jwt);

        List<Category> categories = categoryService.findCategoryByRestaurantId(user.getId());

        log.info("Retrieved categories: {}", categories);

        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    /**
     * Retrieves a category by its ID
     *
     * @param jwt      the JWT token to authenticate the user
     * @param categoryId the ID of the category to retrieve
     * @return the category
     * @throws Exception if the user is not found or if the category is not found
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Category> findCategoryById(@RequestHeader("Authorization") String jwt,
                                                     @PathVariable Long categoryId) throws Exception {
        log.info("Retrieving category with ID: {}", categoryId);

        User user = userService.findUserByJwt(jwt);

        Category category = categoryService.findCategoryById(categoryId);

        log.info("Retrieved category: {}", category);

        return new ResponseEntity<>(category, HttpStatus.OK);
    }

}