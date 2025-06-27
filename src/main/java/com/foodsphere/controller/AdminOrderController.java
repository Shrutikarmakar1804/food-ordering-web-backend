package com.foodsphere.controller;

import com.foodsphere.model.Order;
import com.foodsphere.model.User;
import com.foodsphere.service.OrderService;
import com.foodsphere.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles requests related to orders as an admin.
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;
    private final UserService userService;

    /**
     * Retrieves all orders for a restaurant.
     *
     * @param id           the ID of the restaurant
     * @param orderStatus  the status of the orders to retrieve (optional)
     * @param jwt          the JWT token to authenticate the user
     * @return the list of orders
     * @throws Exception if the user is not found or if the restaurant is not found
     */
    @GetMapping("/order/restaurant/{id}")
    public ResponseEntity<List<Order>> getRestaurantsOrder(@PathVariable Long id,
                                                           @RequestParam(required = false) String orderStatus,
                                                           @RequestHeader("Authorization") String jwt) throws Exception {
        log.info("Retrieving all orders for restaurant with ID {}", id);

        User user = userService.findUserByJwt(jwt);
        List<Order> orders = orderService.getRestaurantsOrder(id, orderStatus);

        log.info("Found {} orders for restaurant with ID {}", orders.size(), id);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    /**
     * Updates the status of an order.
     *
     * @param orderId      the ID of the order to update
     * @param orderStatus  the new status of the order
     * @param jwt          the JWT token to authenticate the user
     * @return the updated order
     * @throws Exception if the user is not found or if the order is not found
     */
    @PutMapping("/order/{orderId}/{orderStatus}")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId,
                                                   @PathVariable String orderStatus,
                                                   @RequestHeader("Authorization") String jwt) throws Exception {
        log.info("Updating order with ID {} to status {}", orderId, orderStatus);

        User user = userService.findUserByJwt(jwt);
        Order order = orderService.updateOrderStatus(orderId, orderStatus);

        log.info("Updated order with ID {} to status {}", orderId, orderStatus);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}