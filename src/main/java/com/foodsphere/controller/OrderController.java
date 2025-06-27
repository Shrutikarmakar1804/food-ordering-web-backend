package com.foodsphere.controller;

import com.foodsphere.model.Order;
import com.foodsphere.model.User;
import com.foodsphere.request.OrderRequest;
import com.foodsphere.response.OrderCancelResponse;
import com.foodsphere.service.OrderService;
import com.foodsphere.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles requests related to orders.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    /**
     * Creates a new order.
     *
     * @param request the order request
     * @param jwt     the JWT token to authenticate the user
     * @return the created order
     * @throws Exception if the user is not found
     */
    @PostMapping("/order")
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request, @RequestHeader("Authorization") String jwt) throws Exception {
        log.info("Creating a new order for user with JWT: {}", jwt);
        User user = userService.findUserByJwt(jwt);
        Order order = orderService.createOrder(request, user);
        log.info("Order created successfully: {}", order);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    /**
     * Retrieves all orders for a user.
     *
     * @param jwt the JWT token to authenticate the user
     * @return the list of orders
     * @throws Exception if the user is not found
     */
    @GetMapping("/order/user")
    public ResponseEntity<List<Order>> getUserOrder(@RequestHeader("Authorization") String jwt) throws Exception {
        log.info("Retrieving orders for user with JWT: {}", jwt);
        User user = userService.findUserByJwt(jwt);
        List<Order> orders = orderService.getUsersOrder(user.getId());
        log.info("Orders retrieved successfully: {}", orders);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    /**
     * Cancels an order.
     *
     * @param jwt      the JWT token to authenticate the user
     * @param orderId  the ID of the order to cancel
     * @return the response
     * @throws Exception if the order is not found
     */
    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<OrderCancelResponse> cancelOrder(@RequestHeader("Authorization") String jwt, @PathVariable Long orderId) throws Exception {
        log.info("Cancelling order with ID: {} for user with JWT: {}", orderId, jwt);
        User user = userService.findUserByJwt(jwt);
        orderService.cancelOrder(orderId);
        OrderCancelResponse response = new OrderCancelResponse("Order cancelled Successfully", true);
        log.info("Order cancelled successfully: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}