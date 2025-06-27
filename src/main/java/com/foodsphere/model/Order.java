package com.foodsphere.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Order entity representing a customer's order in the system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private User customer; // User who placed the order

    @ManyToOne
    @JsonIgnore
    private Restaurant restaurant; // Restaurant from which the order is placed

    // Total amount of the order
    private Long totalAmount;

    // Status of the order (e.g., pending, completed)
    private String orderStatus;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Delivery address for the order
    @ManyToOne
    private Address deliveryAddress;

    /**
     * List of ordered items
     */
    @OneToMany
    private List<OrderItem> items;

    // Uncomment if payment details are to be included
    // private Payment payment;

    // Total number of items in the order
    private int totalItem;

    // Total price of the order
    private Long totalPrice;

}