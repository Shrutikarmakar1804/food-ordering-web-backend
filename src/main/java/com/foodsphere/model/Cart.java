package com.foodsphere.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Cart entity representing a shopping cart.
 * It contains information about the customer, total price, and items in the cart.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * A cart is owned by a single user.
     * A user can have multiple carts, but a cart can only belong to one user.
     */
    @OneToOne
    private User customer;

    private Long total;

    /**
     * A cart can contain multiple items.
     * An item can only exist in one cart.
     * If the cart is deleted, all items in the cart will be deleted as well.
     */
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
}