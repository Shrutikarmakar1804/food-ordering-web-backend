package com.foodsphere.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.foodsphere.model.Category;
import com.foodsphere.model.IngredientsItem;
import com.foodsphere.model.Restaurant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "foods")
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String description;
    private Long price;

    private boolean available;
    private boolean isVegetarian;
    private boolean isSeasonal;

    /**
     * A particular food can belong to multiple categories.
     * This is @ManyToOne if food has one category.
     * Removed cascade to avoid 'detached entity' issue.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Category foodCategory;

    @ElementCollection
    @Column(length = 1000)
    private List<String> images = new ArrayList<>();

    /**
     * Multiple food items can belong to one restaurant.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Restaurant restaurant;

    /**
     * A food can have many ingredients and vice versa.
     * Removed cascade to avoid accidental persistence of detached entities.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<IngredientsItem> ingredients = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime creationAt;
}
