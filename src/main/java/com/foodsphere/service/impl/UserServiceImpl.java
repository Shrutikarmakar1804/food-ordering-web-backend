package com.foodsphere.service.impl;

import com.foodsphere.config.JwtProvider;
import com.foodsphere.model.User;
import com.foodsphere.repository.UserRepository;
import com.foodsphere.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * This class is the implementation of the UserService interface.
 * This class is implementing the findUserByJwt and findUserByEmail methods.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Override
    public User findUserByJwt(String jwt) throws Exception {
        String email = getEmailFromJwt(jwt);
        log.debug("Attempting to find user with email {}", email);

        User user = userRepository.findByEmail(email);

        if (user == null) {
            log.error("User not found with email {}", email);
            throw new Exception("User not found with email " + email);
        }

        return user;
    }

    @Override
    public User findUserByEmail(String email) throws Exception {
        log.debug("Attempting to find user with email {}", email);

        User user = userRepository.findByEmail(email);

        if (user == null) {
            log.error("User not found with email {}", email);
            throw new Exception("User not found with email " + email);
        }

        return user;
    }

    private String getEmailFromJwt(String jwt) {
        log.debug("Extracting email from JWT token");
        return jwtProvider.getEmailFromJwt(jwt);
    }

}