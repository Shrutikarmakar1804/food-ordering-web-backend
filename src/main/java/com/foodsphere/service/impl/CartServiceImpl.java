package com.foodsphere.service.impl;

import com.foodsphere.model.Cart;
import com.foodsphere.model.CartItem;
import com.foodsphere.model.Food;
import com.foodsphere.model.User;
import com.foodsphere.repository.CartItemRepository;
import com.foodsphere.repository.CartRepository;
import com.foodsphere.request.AddCartItemRequest;
import com.foodsphere.service.CartService;
import com.foodsphere.service.FoodService;
import com.foodsphere.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the CartService interface, providing business logic for managing shopping carts.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserService userService;
    private final CartItemRepository cartItemRepository;
    private final FoodService foodService;

    /**
     * Adds a new item to the cart of a user.
     *
     * @param request  the request object containing the details of the item to add
     * @param jwt      the JWT token of the user
     * @return the newly created cart item
     * @throws Exception if the user is not found or if the food item is not found
     */
    @Override
    public CartItem addItemToCart(AddCartItemRequest request, String jwt) throws Exception {
        log.info("Adding item to cart for user with jwt: {}", jwt);
        User user = userService.findUserByJwt(jwt);
        Food food = foodService.findFoodById(request.getFoodId());
        Cart cart = cartRepository.findByCustomerId(user.getId());

        // Check if the food item already exists in the cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getFood().equals(food))
                .findFirst();

        if (existingItem.isPresent()) {
            log.info("Updating quantity of existing item in cart");
            CartItem existingCartItem = existingItem.get();
            int newQuantity = existingCartItem.getQuantity() + request.getQuantity();
            return updateCartItemQuantity(existingCartItem.getId(), newQuantity);
        }

        // Create a new cart item
        log.info("Creating new item in cart");
        CartItem newCartItem = new CartItem();

        newCartItem.setFood(food);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(request.getQuantity());
        newCartItem.setIngredients(request.getIngredients());
        newCartItem.setTotalPrice(food.getPrice() * request.getQuantity());

        CartItem savedCartItem = cartItemRepository.save(newCartItem);

        cart.getItems().add(savedCartItem);

        return savedCartItem;
    }

    /**
     * Updates the quantity of a cart item.
     *
     * @param cartItemId the ID of the cart item to update
     * @param quantity   the new quantity of the cart item
     * @return the updated cart item
     * @throws Exception if the cart item is not found
     */
    @Override
    public CartItem updateCartItemQuantity(Long cartItemId, int quantity) throws Exception {
        log.info("Updating quantity of cart item with id: {}", cartItemId);
        Optional<CartItem> optionalCartItem = cartItemRepository.findById(cartItemId);
        if (optionalCartItem.isEmpty()) {
            log.error("Cart item not found with id: {}", cartItemId);
            throw new Exception("Cart item not found with id: " + cartItemId);
        }

        CartItem item = optionalCartItem.get();

        item.setQuantity(quantity);
        item.setTotalPrice(item.getFood().getPrice() * quantity);

        return cartItemRepository.save(item);
    }

    /**
     * Removes a cart item from the cart of a user.
     *
     * @param cartItemId the ID of the cart item to remove
     * @param jwt        the JWT token of the user
     * @return the updated cart
     * @throws Exception if the user is not found or if the cart item is not found
     */
    @Override
    public Cart removeItemFromCart(Long cartItemId, String jwt) throws Exception {
        log.info("Removing item from cart for user with jwt: {}", jwt);
        User user = userService.findUserByJwt(jwt);
        Cart cart = cartRepository.findByCustomerId(user.getId());

        Optional<CartItem> optionalCartItem = cartItemRepository.findById(cartItemId);
        if (optionalCartItem.isEmpty()) {
            log.error("Cart item not found with id: {}", cartItemId);
            throw new Exception("Cart item not found with id: " + cartItemId);
        }

        CartItem item = optionalCartItem.get();
        cart.getItems().remove(item);

        return cartRepository.save(cart);
    }

    /**
     * Calculates the total price of all items in a cart.
     *
     * @param cart the cart to calculate the total for
     * @return the total price of all items in the cart
     */
    @Override
    public Long calculateCartTotals(Cart cart) {
        log.info("Calculating total for cart: {}", cart);
        Long total = 0L;

        for (CartItem cartItem : cart.getItems()) {
            total += cartItem.getFood().getPrice() * cartItem.getTotalPrice();
        }
        return total;
    }

    /**
     * Finds a cart by its ID.
     *
     * @param id the ID of the cart to find
     * @return the cart with the given ID
     * @throws Exception if the cart is not found
     */
    @Override
    public Cart findCartById(Long id) throws Exception {
        log.info("Finding cart by id: {}", id);
        Optional<Cart> optionalCart = cartRepository.findById(id);
        if (optionalCart.isEmpty()) {
            log.error("Cart not found with id: {}", id);
            throw new Exception("Cart not found with id: " + id);
        }

        return optionalCart.get();
    }

    /**
     * Finds a cart by the ID of the user it belongs to.
     *
     * @param userId the ID of the user to find the cart for
     * @return the cart belonging to the user with the given ID
     * @throws Exception if the user is not found
     */
    @Override
    public Cart findCartByUserId(Long userId) throws Exception {
        log.info("Finding cart by user id: {}", userId);
        Cart cart = cartRepository.findByCustomerId(userId);
        cart.setTotal(calculateCartTotals(cart));

        return cart;
    }

    /**
     * Clears the cart of a user.
     *
     * @param userId the ID of the user to clear the cart for
     * @return the updated cart
     * @throws Exception if the user is not found
     */
    @Override
    public Cart clearCart(Long userId) throws Exception {
        log.info("Clearing cart for user with id: {}", userId);
        Cart cart = findCartByUserId(userId);
        cart.getItems().clear();

        return cartRepository.save(cart);
    }
}