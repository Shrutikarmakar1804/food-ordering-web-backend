package com.foodsphere.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * IngredientsItem entity represents an ingredient item.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ingredients_item")
public class IngredientsItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    /**
     * The category of the ingredient item.
     * This is a ManyToOne relationship with IngredientCategory.
     * We use JsonBackReference to prevent recursive reference from IngredientCategory.
     */
    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonBackReference
    @ToString.Exclude
    private IngredientCategory category;

    /**
     * The restaurant of the ingredient item.
     * This is a ManyToOne relationship with Restaurant.
     * We use JsonIgnore to prevent serialization of the restaurant.
     */
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    @JsonIgnore
    @ToString.Exclude
    private Restaurant restaurant;

    /**
     * Whether the ingredient item is in stock or not.
     */
    private boolean isStock = true;

}
