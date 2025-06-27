package com.foodsphere.request;

import com.foodsphere.model.Category;
import com.foodsphere.model.IngredientsItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request object for creating a new food item.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateFoodRequest {

    private Long restaurantId;
    private String name;
    private String description;
    private Long price;
    private boolean seasonal;
    private boolean vegetarian;
    private Category category;

    private List<String> images;
    private List<IngredientsItem> ingredients;

}