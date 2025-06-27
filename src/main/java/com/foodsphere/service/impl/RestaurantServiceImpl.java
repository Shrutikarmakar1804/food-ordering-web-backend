package com.foodsphere.service.impl;

import com.foodsphere.dto.RestaurantDto;
import com.foodsphere.model.Address;
import com.foodsphere.model.Restaurant;
import com.foodsphere.model.User;
import com.foodsphere.repository.AddressRepository;
import com.foodsphere.repository.RestaurantRepository;
import com.foodsphere.repository.UserRepository;
import com.foodsphere.request.CreateRestaurantRequest;
import com.foodsphere.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the RestaurantService interface, providing business logic
 * for managing restaurants including creation, updates, deletion, and retrieval.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    /**
     * Creates a new restaurant entity.
     *
     * @param createRestaurantRequest the request object containing restaurant details
     * @param user the user who owns the restaurant
     * @return the created restaurant
     */
    @Override
    public Restaurant createRestaurant(CreateRestaurantRequest createRestaurantRequest, User user) {
        log.info("Creating a new restaurant for user: {}", user.getId());

        validateAddressFields(createRestaurantRequest.getAddress());
        Address address = addressRepository.save(createRestaurantRequest.getAddress());

        Restaurant restaurant = new Restaurant();

        restaurant.setName(createRestaurantRequest.getName());
        restaurant.setDescription(createRestaurantRequest.getDescription());
        restaurant.setCuisineType(createRestaurantRequest.getCuisineType());
        restaurant.setOpeningHours(createRestaurantRequest.getOpeningHours());
        restaurant.setRegistrationDate(LocalDateTime.now());
        restaurant.setAddress(address);
        restaurant.setContactInformation(createRestaurantRequest.getContactInformation());
        restaurant.setImages(createRestaurantRequest.getImages());
        restaurant.setOwner(user);

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        log.info("Restaurant created with ID: {}", savedRestaurant.getId());

        return savedRestaurant;
    }

    /**
     * Validates address fields to ensure none are null.
     *
     * @param address the address to validate
     */
    private void validateAddressFields(Address address) {
        if (address.getCity() == null) {
            log.error("Address city cannot be null");
            throw new IllegalArgumentException("City cannot be null");
        }
    }

    /**
     * Updates an existing restaurant's details.
     *
     * @param restaurantId the ID of the restaurant to update
     * @param updatedRestaurant the request object containing updated details
     * @return the updated restaurant
     * @throws Exception if the restaurant is not found
     */
    @Override
    public Restaurant updateRestaurant(Long restaurantId, CreateRestaurantRequest updatedRestaurant) throws Exception {
        log.info("Updating restaurant with ID: {}", restaurantId);

        Restaurant restaurant = findRestaurantById(restaurantId);

        if (updatedRestaurant.getName() != null) restaurant.setName(updatedRestaurant.getName());
        if (updatedRestaurant.getDescription() != null) restaurant.setDescription(updatedRestaurant.getDescription());
        if (updatedRestaurant.getAddress() != null) restaurant.setAddress(updatedRestaurant.getAddress());
        if (updatedRestaurant.getCuisineType() != null) restaurant.setCuisineType(updatedRestaurant.getCuisineType());
        if (updatedRestaurant.getOpeningHours() != null) restaurant.setOpeningHours(updatedRestaurant.getOpeningHours());

        Restaurant updatedEntity = restaurantRepository.save(restaurant);
        log.info("Restaurant updated with ID: {}", updatedEntity.getId());

        return updatedEntity;
    }

    /**
     * Deletes a restaurant by ID.
     *
     * @param restaurantId the ID of the restaurant to delete
     * @throws Exception if the restaurant is not found
     */
    @Override
    public void deleteRestaurant(Long restaurantId) throws Exception {
        log.info("Deleting restaurant with ID: {}", restaurantId);

        Restaurant restaurant = findRestaurantById(restaurantId);
        restaurantRepository.delete(restaurant);

        log.info("Restaurant with ID {} deleted", restaurantId);
    }

    /**
     * Retrieves all restaurants.
     *
     * @return a list of all restaurants
     */
    @Override
    public List<Restaurant> getAllRestaurant() {
        log.info("Fetching all restaurants");
        return restaurantRepository.findAll();
    }

    /**
     * Searches for restaurants by a keyword.
     *
     * @param keyword the search keyword
     * @return a list of matching restaurants
     */
    @Override
    public List<Restaurant> searchRestaurants(String keyword) {
        log.info("Searching for restaurants with keyword: {}", keyword);
        return restaurantRepository.findBySearchQuery(keyword);
    }

    /**
     * Finds a restaurant by its ID.
     *
     * @param restaurantId the ID of the restaurant
     * @return the found restaurant
     * @throws Exception if the restaurant is not found
     */
    @Override
    public Restaurant findRestaurantById(Long restaurantId) throws Exception {
        log.info("Finding restaurant with ID: {}", restaurantId);

        Optional<Restaurant> selectedRestaurant = restaurantRepository.findById(restaurantId);
        if (selectedRestaurant.isEmpty()) {
            log.error("Restaurant not found with ID: {}", restaurantId);
            throw new Exception("Restaurant not found with id: " + restaurantId);
        }

        log.info("Restaurant found with ID: {}", restaurantId);
        return selectedRestaurant.get();
    }

    /**
     * Finds a restaurant by the owner's user ID.
     *
     * @param userId the owner's user ID
     * @return the found restaurant
     * @throws Exception if the restaurant is not found
     */
    @Override
    public Restaurant findRestaurantByUserId(Long userId) throws Exception {
        log.info("Finding restaurant for owner with user ID: {}", userId);

        Restaurant restaurant = restaurantRepository.findByOwnerId(userId);
        if (restaurant == null) {
            log.error("Restaurant not found with owner ID: {}", userId);
            throw new Exception("Restaurant not found with Owner id: " + userId);
        }

        log.info("Restaurant found for owner with user ID: {}", userId);
        return restaurant;
    }

    /**
     * Adds or removes a restaurant from a user's favourites.
     *
     * @param restaurantId the ID of the restaurant
     * @param user the user whose favourites are being updated
     * @return the restaurant DTO added or removed
     * @throws Exception if the restaurant is not found
     */
    @Override
    public RestaurantDto addToFavourites(Long restaurantId, User user) throws Exception {
        log.info("Updating favourites for user: {}", user.getId());

        Restaurant restaurant = findRestaurantById(restaurantId);
        RestaurantDto favoriteRestaurantDto = new RestaurantDto();
        favoriteRestaurantDto.setDescription(restaurant.getDescription());
        favoriteRestaurantDto.setImages(restaurant.getImages());
        favoriteRestaurantDto.setTitle(restaurant.getName());
        favoriteRestaurantDto.setId(restaurantId);

        boolean isFavourite = user.getFavourites().stream().anyMatch(fav -> fav.getId().equals(restaurantId));

        if (isFavourite) {
            user.getFavourites().removeIf(fav -> fav.getId().equals(restaurantId));
            log.info("Restaurant with ID {} removed from favourites", restaurantId);
        } else {
            user.getFavourites().add(favoriteRestaurantDto);
            log.info("Restaurant with ID {} added to favourites", restaurantId);
        }

        userRepository.save(user);
        return favoriteRestaurantDto;
    }

    /**
     * Toggles the open status of a restaurant.
     *
     * @param id the ID of the restaurant
     * @return the updated restaurant
     * @throws Exception if the restaurant is not found
     */
    @Override
    public Restaurant updateRestaurantStatus(Long id) throws Exception {
        log.info("Toggling open status for restaurant with ID: {}", id);

        Restaurant restaurant = findRestaurantById(id);
        restaurant.setOpen(!restaurant.isOpen());

        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        log.info("Restaurant open status updated for ID: {}", id);

        return updatedRestaurant;
    }
}