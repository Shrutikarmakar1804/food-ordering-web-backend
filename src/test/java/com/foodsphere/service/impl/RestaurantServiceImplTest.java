package com.foodsphere.service.impl;

import com.foodsphere.model.Address;
import com.foodsphere.model.ContactInformation;
import com.foodsphere.model.Restaurant;
import com.foodsphere.model.User;
import com.foodsphere.repository.AddressRepository;
import com.foodsphere.repository.RestaurantRepository;
import com.foodsphere.repository.UserRepository;
import com.foodsphere.request.CreateRestaurantRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceImplTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    @Test
    void createRestaurant_createsRestaurantSuccessfully() {
        CreateRestaurantRequest request = getRestaurantRequest();

        User user = new User();
        user.setId(1L);

        Address savedAddress = new Address(1L, "Test City", "Kolkata", "india", "demo");
        savedAddress.setId(1L);

        Restaurant savedRestaurant = new Restaurant();
        savedRestaurant.setId(1L);

        Mockito.when(addressRepository.save(request.getAddress())).thenReturn(savedAddress);
        Mockito.when(restaurantRepository.save(Mockito.any(Restaurant.class))).thenReturn(savedRestaurant);

        Restaurant result = restaurantService.createRestaurant(request, user);

        assertNotNull(result);
        assertEquals(savedRestaurant.getId(), result.getId());
        Mockito.verify(addressRepository).save(request.getAddress());
        Mockito.verify(restaurantRepository).save(Mockito.any(Restaurant.class));
    }

    private static CreateRestaurantRequest getRestaurantRequest() {
        CreateRestaurantRequest request = new CreateRestaurantRequest();
        request.setName("Test Restaurant");
        request.setDescription("Test Description");
        request.setCuisineType("Italian");
        request.setOpeningHours("9 AM - 9 PM");
        request.setAddress(new Address(1L, "Test City", "Kolkata", "india", "demo"));
        request.setContactInformation(new ContactInformation("demo@gmail.com", "123-456-7890", "facebook.com"));
        request.setImages(List.of("image1.jpg", "image2.jpg"));
        return request;
    }

}