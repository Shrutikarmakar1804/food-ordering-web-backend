package com.foodsphere.controller;

import com.foodsphere.model.Cart;
import com.foodsphere.model.CartItem;
import com.foodsphere.model.User;
import com.foodsphere.request.AddCartItemRequest;
import com.foodsphere.request.UpdateCartItemRequest;
import com.foodsphere.service.CartService;
import com.foodsphere.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Handles requests related to user carts.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    /**
     * Adds a new item to the cart of a user.
     *
     * @param request the request object containing the details of the item to add
     * @param jwt     the JWT token of the user
     * @return the newly created cart item
     * @throws Exception if the user is not found, or if the food item is not found
     */
    @PostMapping("/cart/add")
    public ResponseEntity<CartItem> addItemToCart(@RequestBody @Valid AddCartItemRequest request, @RequestHeader("Authorization") String jwt) throws Exception {
        log.info("Adding item to cart for user with JWT: {}", jwt);
        CartItem cartItem = cartService.addItemToCart(request, jwt);

        return new ResponseEntity<>(cartItem, HttpStatus.CREATED);
    }

    /**
     * Updates the quantity of a cart item.
     *
     * @param request the request object containing the ID of the cart item and the new quantity
     * @param jwt     the JWT token of the user
     * @return the updated cart item
     * @throws Exception if the cart item is not found
     */
    @PutMapping("/cart-item/update")
    public ResponseEntity<CartItem> updateCartItemQuantity(@RequestBody @Valid UpdateCartItemRequest request, @RequestHeader("Authorization") String jwt) throws Exception {
        log.info("Updating cart item with ID: {} for user with JWT: {}", request.getCartItemId(), jwt);
        CartItem cartItem = cartService.updateCartItemQuantity(request.getCartItemId(), request.getQuantity());

        return new ResponseEntity<>(cartItem, HttpStatus.OK);
    }

    /**
     * Removes a cart item from the cart of a user.
     *
     * @param id the ID of the cart item to remove
     * @param jwt the JWT token of the user
     * @return the updated cart
     * @throws Exception if the user is not found, or if the cart item is not found
     */
    @DeleteMapping("/cart-item/{id}/remove")
    public ResponseEntity<Cart> removeCartItem(@PathVariable Long id, @RequestHeader("Authorization") String jwt) throws Exception {
        log.info("Removing cart item with ID: {} for user with JWT: {}", id, jwt);
        Cart cart = cartService.removeItemFromCart(id, jwt);

        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    /**
     * Clears the cart of a user.
     *
     * @param jwt the JWT token of the user
     * @return the updated cart
     * @throws Exception if the user is not found
     */
    @DeleteMapping("/cart/clear")
    public ResponseEntity<Cart> clearCart(@RequestHeader("Authorization") String jwt) throws Exception {
        log.info("Clearing cart for user with JWT: {}", jwt);
        User user = userService.findUserByJwt(jwt);
        Cart cart = cartService.clearCart(user.getId());

        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    /**
     * Finds the cart of a user.
     *
     * @param jwt the JWT token of the user
     * @return the cart of the user
     * @throws Exception if the user is not found
     */
    @GetMapping("/cart")
    public ResponseEntity<Cart> findUserCart(@RequestHeader("Authorization") String jwt) throws Exception {
        log.info("Finding cart for user with JWT: {}", jwt);
        User user = userService.findUserByJwt(jwt);

        Optional<Cart> cartOptional = Optional.ofNullable(cartService.findCartByUserId(user.getId()));
        if (cartOptional.isEmpty()) {
            log.error("User with JWT: {} has no cart", jwt);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Cart cart = cartOptional.get();
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

}