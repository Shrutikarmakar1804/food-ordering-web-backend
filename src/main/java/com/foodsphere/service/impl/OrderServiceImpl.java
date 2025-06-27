package com.foodsphere.service.impl;

import com.foodsphere.model.*;
import com.foodsphere.repository.AddressRepository;
import com.foodsphere.repository.OrderItemRepository;
import com.foodsphere.repository.OrderRepository;
import com.foodsphere.repository.UserRepository;
import com.foodsphere.request.OrderRequest;
import com.foodsphere.service.CartService;
import com.foodsphere.service.OrderService;
import com.foodsphere.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the OrderService interface, providing business logic
 * for managing orders including creation, updates, deletion, and retrieval.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final RestaurantService restaurantService;
    private final CartService cartService;

    @Override
    public Order createOrder(OrderRequest order, User user) throws Exception {
        log.info("Creating order for user: {}", user.getFullName());

        Address shippingAddress = order.getDeliveryAddress();
        Address savedAddress = addressRepository.save(shippingAddress);

        if(!user.getAddresses().contains(savedAddress)){
            user.getAddresses().add(savedAddress);
            userRepository.save(user);
        }

        Restaurant restaurant = restaurantService.findRestaurantById(order.getRestaurantId());
        Order createdOrder = new Order();

        createdOrder.setCustomer(user);
        createdOrder.setCreatedAt(LocalDateTime.now());
        createdOrder.setOrderStatus("PENDING");
        createdOrder.setDeliveryAddress(savedAddress);
        createdOrder.setRestaurant(restaurant);

        Cart cart = cartService.findCartByUserId(user.getId());

        List<OrderItem> orderItems = new ArrayList<>();

        for(CartItem cartItem : cart.getItems()){
            OrderItem orderItem = new OrderItem();

            orderItem.setFood(cartItem.getFood());
            orderItem.setIngredients(cartItem.getIngredients());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(cartItem.getTotalPrice());

            OrderItem savedOrderItem = orderItemRepository.save(orderItem);

            orderItems.add(savedOrderItem);
        }

        Long totalPrice = cartService.calculateCartTotals(cart);

        createdOrder.setItems(orderItems);
        createdOrder.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(createdOrder);

        restaurant.getOrders().add(savedOrder);

        log.info("Order created successfully: {}", savedOrder.getId());

        return createdOrder;
    }

    @Override
    public Order updateOrderStatus(Long orderId, String orderStatus) throws Exception {
        log.info("Updating order status for order: {}", orderId);

        Order order = findOrderById(orderId);

        if(orderStatus.equals("OUT_FOR_DELIVERY") || orderStatus.equals("DELIVERED") || orderStatus.equals("COMPLETED")|| orderStatus.equals("PENDING")){
            order.setOrderStatus(orderStatus);
            return orderRepository.save(order);
        }
        throw  new Exception("Invalid Order Status");
    }

    @Override
    public void cancelOrder(Long orderId) throws Exception {
        log.info("Cancelling order: {}", orderId);

        Order order = findOrderById(orderId);
        orderRepository.deleteById(orderId);
    }

    @Override
    public List<Order> getUsersOrder(Long userId) throws Exception {
        log.info("Fetching orders for user: {}", userId);

        return orderRepository.findByCustomerId(userId);
    }

    @Override
    public List<Order> getRestaurantsOrder(Long restaurantId, String orderStatus) throws Exception {
        log.info("Fetching orders for restaurant: {}", restaurantId);

        List<Order> orders = orderRepository.findByRestaurantId(restaurantId);
        if(orderStatus != null){
            orders = orders.stream().filter(order -> order.getOrderStatus().equals(orderStatus)).collect(Collectors.toList());
        }
        return orders;
    }

    @Override
    public Order findOrderById(Long orderId) throws Exception {
        log.info("Fetching order by id: {}", orderId);

        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if(optionalOrder.isEmpty()){
            throw new Exception("Order not found");
        }
        return optionalOrder.get();
    }

}