package com.foodsphere.repository;

import com.foodsphere.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Query(
            nativeQuery = true,
            value = "SELECT * FROM restaurants WHERE LOWER(name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(cuisine_type) LIKE LOWER(CONCAT('%', :query, '%'))"
    )
    List<Restaurant> findBySearchQuery(String query);


    Restaurant findByOwnerId(Long userId);
}
