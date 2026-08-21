package com.stp.stPlatform.service;

import com.stp.stPlatform.domain.OrderType;
import com.stp.stPlatform.model.Coin;
import com.stp.stPlatform.model.Order;
import com.stp.stPlatform.model.OrderItem;
import com.stp.stPlatform.model.User;

import java.util.List;

public interface OrderService {
    Order createOrder(User user, OrderItem orderItem, OrderType orderType);
    Order getOrderById(Long orderId);
    List<Order> getAllOrdersOfUser(Long userId, OrderType orderType, String assetSymbol);
    Order processOrder(Coin coin, double quantity, OrderType orderType, User user) throws Exception;
}