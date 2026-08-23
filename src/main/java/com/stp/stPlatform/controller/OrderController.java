package com.stp.stPlatform.controller;

import com.stp.stPlatform.domain.OrderType;
import com.stp.stPlatform.model.Coin;
import com.stp.stPlatform.model.Order;
import com.stp.stPlatform.model.User;
import com.stp.stPlatform.request.CreateOrderRequest;
import com.stp.stPlatform.service.CoinService;
import com.stp.stPlatform.service.OrderService;
import com.stp.stPlatform.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final CoinService coinService;

    public OrderController(OrderService orderService,
                           UserService userService,
                           CoinService coinService) {
        this.orderService = orderService;
        this.userService = userService;
        this.coinService = coinService;
    }

    @PostMapping("/pay")
    public ResponseEntity<Order> payOrderPayment(
            @RequestHeader("Authorization") String jwt,
            @RequestBody CreateOrderRequest req) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);
        Coin coin = coinService.findById(req.getCoinId());

        Order order = orderService.processOrder(coin, req.getQuantity(), req.getOrderType(), user);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long orderId) {

        User user = userService.findUserProfileByJwt(jwt);
        Order order = orderService.getOrderById(orderId);

        if (!Objects.equals(order.getUser().getId(), user.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrdersForUser(
            @RequestHeader("Authorization") String jwt,
            @RequestParam(required = false) OrderType order_type,
            @RequestParam(required = false) String asset_symbol) {

        User user = userService.findUserProfileByJwt(jwt);
        List<Order> userOrders = orderService.getAllOrdersOfUser(user.getId(), order_type, asset_symbol);

        return new ResponseEntity<>(userOrders, HttpStatus.OK);
    }
}