package com.foodsphere.request;

import com.foodsphere.model.Address;
import com.foodsphere.model.ContactInformation;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateRestaurantRequest {

    @NotNull
    private String name;

    @NotNull
    private String description;

    private String cuisineType;

    @NotNull
    private Address address;

    private ContactInformation contactInformation;

    private String openingHours;

    private List<String> images;

}
