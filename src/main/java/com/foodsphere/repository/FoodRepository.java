package com.foodsphere.repository;

import com.foodsphere.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Long> {

    List<Food> findByRestaurantId(Long restaurantId);

    @Query(
        nativeQuery = true,
        value = "SELECT * FROM food WHERE LOWER(name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(food_category) LIKE LOWER(CONCAT('%', :keyword, '%'))"
    )
    List<Food> searchFood(@Param("keyword") String keyword);
}