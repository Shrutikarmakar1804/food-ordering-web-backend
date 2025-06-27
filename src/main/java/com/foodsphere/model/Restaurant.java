package com.foodsphere.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Restaurant Entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String description;
    private String cuisineType;
    private String openingHours;
    private boolean open;

    private LocalDateTime registrationDate;

    /**
     * Owner of the restaurant.
     * We build the Backend in such a way, One Owner can have exactly one Restaurant.
     */
    @OneToOne
    @ToString.Exclude
    private User owner;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    @ToString.Exclude
    private Address address;

    @Embedded
    private ContactInformation contactInformation;

    /**
     * List of orders associated with the restaurant.
     * One Restaurant can have multiple orders.
     */
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Order> orders = new ArrayList<>();

    /**
     * List of images associated with the restaurant and
     * A restaurant can have multiple images.
     */
    @ElementCollection
    @Column(length = 1000)
    private List<String> images;

    /**
     * List of foods associated with the restaurant
     * This field is not serialized to prevent recursive relationships
     */
    @JsonIgnore
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Food> foods = new ArrayList<>();

}
