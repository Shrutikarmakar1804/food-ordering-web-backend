package com.foodsphere.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * CartItem Model
 * Represents an item in a shopping cart, including details about the food item, its quantity, 
 * associated ingredients, and the total price for the item. 
 * A CartItem is linked to a specific Cart.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The Cart to which this item belongs. 
     * Many items are associated with a particular cart.
     */
    @ManyToOne @JsonIgnore
    private Cart cart;

    /**
     * The Food item associated with this CartItem.
     */
    @ManyToOne
    private Food food;

    private int quantity;

    /**
     * List of ingredient names associated with this CartItem.
     */
    private List<String> ingredients;

    private Long totalPrice;
    
}