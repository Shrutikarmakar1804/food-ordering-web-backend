package com.foodsphere.controller;

import com.foodsphere.config.JwtProvider;
import com.foodsphere.model.Cart;
import com.foodsphere.model.USER_ROLE;
import com.foodsphere.model.User;
import com.foodsphere.repository.CartRepository;
import com.foodsphere.repository.UserRepository;
import com.foodsphere.request.LoginRequest;
import com.foodsphere.response.AuthenticationResponse;
import com.foodsphere.service.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * This class handles the authentication process including user registration and login.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;

    private final UserDetailsServiceImpl userDetailsService;

    private final AuthenticationManager authenticationManager;

    /**
     * This method handles the user registration process.
     * @param user The user data to be registered.
     * @return A response containing the JWT token and a success message.
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthenticationResponse> createUserHandler(@RequestBody User user){
        log.info("Creating a new user with email {}", user.getEmail());

        // Check if the user already exists.
        if(userRepository.findByEmail(user.getEmail()) != null){
            log.error("Email: {} already in use.", user.getEmail());
            throw new RuntimeException("Email already in use. Please use a different email."+ user.getEmail());
        }

        // Create a new user and save it to the database.
        User createdUser = new User();

        createdUser.setEmail(user.getEmail());
        createdUser.setPassword(passwordEncoder.encode(user.getPassword()));
        createdUser.setFullName(user.getFullName());
        createdUser.setRole(user.getRole());

        User savedUser = userRepository.save(createdUser);
        log.info("User data saved Successfully to the database with email {}", user.getEmail());

        // Create a new cart for the user and save it to the database.
        Cart cart = new Cart();
        cart.setCustomer(savedUser);
        cartRepository.save(cart);
        log.info("Cart Created Successfully for the user with email {}", user.getEmail());

        // Authenticate the user and generate a JWT token.
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateToken(authentication);

        AuthenticationResponse response = new AuthenticationResponse();
        response.setJwt(jwt);
        response.setMessage("Your account has been created successfully");
        response.setRole(savedUser.getRole());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * This method handles the user login process.
     * @param request The login request data.
     * @return A response containing the JWT token and a success message.
     */
    @PostMapping("/signin")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request){
        String email = request.getEmail();
        String password = request.getPassword();

        log.info("Logging in user with email {}", email);

        // Authenticate the user and generate a JWT token.
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();

        String jwt = jwtProvider.generateToken(authentication);

        AuthenticationResponse response = new AuthenticationResponse();
        response.setJwt(jwt);
        response.setMessage("Your account has been login successfully");
        response.setRole(USER_ROLE.valueOf(role));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * This method authenticates the user and returns an authentication object.
     * @param email The user email.
     * @param password The user password.
     * @return An authentication object.
     */
    private Authentication authenticate(String email, String password) {
        log.info("Authenticating user with email {}", email);

        // Check if the user exists.
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if(userDetails == null){
            log.error("User not available with email {}", email);
            throw new BadCredentialsException("Invalid email.");
        }

        // Check if the password is correct.
        if(!passwordEncoder.matches(password, userDetails.getPassword())){
            log.error("Invalid Password {}", password);
            throw new BadCredentialsException("Invalid Password");
        }

        // Return the authentication object.
        log.info("User authenticated successfully with email {}", email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<AuthenticationResponse> handleBadCredentialsException(BadCredentialsException ex) {
        log.error("Bad Credentials Exception", ex);
        AuthenticationResponse response = new AuthenticationResponse();
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<AuthenticationResponse> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime Exception", ex);
        AuthenticationResponse response = new AuthenticationResponse();
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}