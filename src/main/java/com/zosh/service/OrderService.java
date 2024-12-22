package com.zosh.service;

import java.util.List;
import java.util.Set;

import com.zosh.domain.OrderStatus;
import com.zosh.modal.Address;
import com.zosh.modal.Cart;
import com.zosh.modal.Order;
import com.zosh.modal.OrderItem;
import com.zosh.modal.User;

public interface OrderService {
    Set<Order> createOrder(User user, Address shippingAddress, Cart cart);

    Order findOrderById(long id) throws Exception;

    List<Order> usersOrderHistory(Long userId);

    List<Order> sellersOrder(Long sellerId);

    Order updateOrderStatus(Long orderId, OrderStatus OrderStatus) throws Exception;

    Order cancelOrder(Long orderId, User user) throws Exception;

    OrderItem getOrderItemById(Long id) throws Exception;
    // 10h51,45

}
