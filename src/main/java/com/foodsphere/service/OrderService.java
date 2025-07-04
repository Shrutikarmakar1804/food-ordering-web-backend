package com.foodsphere.service;

import com.foodsphere.model.Order;
import com.foodsphere.model.User;
import com.foodsphere.request.OrderRequest;

import java.util.List;

public interface OrderService {

    public Order createOrder(OrderRequest order, User user) throws Exception;

    public List<Order> getUsersOrder(Long userId) throws Exception;

    public void cancelOrder(Long orderId) throws Exception;

    public Order updateOrderStatus(Long orderId, String orderStatus) throws Exception;

    public List<Order> getRestaurantsOrder(Long restaurantId, String orderStatus) throws Exception;

    public Order findOrderById(Long orderId) throws Exception;

}
