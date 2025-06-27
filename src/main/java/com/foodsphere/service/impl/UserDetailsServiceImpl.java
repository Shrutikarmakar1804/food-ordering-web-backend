package com.foodsphere.service.impl;

import com.foodsphere.model.USER_ROLE;
import com.foodsphere.model.User;
import com.foodsphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for loading the user details from the database.
 * <p>
 * By implementing this class, we are telling the Spring Security to not generate the auto password.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Load the user from the database
        User user = userRepository.findByEmail(email);

        // Check if the user exists
        if (user == null) {
            log.error("User not found with email {}", email);
            throw new UsernameNotFoundException(String.format("User not found with email %s", email));
        }

        // Get the user role
        USER_ROLE role = user.getRole();

        // Create a list of granted authorities
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.toString()));

        // Create the user details object
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }
}