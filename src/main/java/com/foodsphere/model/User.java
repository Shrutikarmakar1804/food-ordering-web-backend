package com.foodsphere.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.foodsphere.dto.RestaurantDto;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * User Model
 * This class represents the User entity in the database.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    // Unique identifier for the user
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Invalid email address")
    private String email;

    /**
     * Password for the user.
     * 
     * Note: We are using @JsonProperty to exclude the password from the JSON
     * response, as we don't want to expose the password.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "Password is required")
    private String password;

    /**
     * Role of the user.
     * We are setting the default role to CUSTOMER
     */
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role is required")
    private USER_ROLE role = USER_ROLE.ROLE_CUSTOMER;

    /**
     * List of orders made by the user.
     * We are using @JsonIgnore to exclude the orders from the JSON response,
     * as we don't want to expose the orders.
     */
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
    private List<Order> orders = new ArrayList<>();

    /**
     * List of favourite restaurants of the user.
     */
    @ElementCollection
    private List<RestaurantDto> favourites = new ArrayList<>();

    /**
     * List of addresses of the user.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

}