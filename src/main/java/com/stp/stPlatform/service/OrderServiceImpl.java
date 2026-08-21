package com.stp.stPlatform.service;

import com.stp.stPlatform.domain.OrderStatus;
import com.stp.stPlatform.domain.OrderType;
import com.stp.stPlatform.model.*;
import com.stp.stPlatform.repository.AssetRepository;
import com.stp.stPlatform.repository.OrderItemRepository;
import com.stp.stPlatform.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AssetRepository assetRepository;
    private final WalletService walletService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            AssetRepository assetRepository,
                            WalletService walletService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.assetRepository = assetRepository;
        this.walletService = walletService;
    }

    @Override
    public Order createOrder(User user, OrderItem orderItem, OrderType orderType) {
        double price = orderItem.getCoin().getCurrentPrice() * orderItem.getQuantity();

        Order order = new Order();
        order.setUser(user);
        order.setOrderItem(orderItem);
        order.setOrderType(orderType);
        order.setPrice(BigDecimal.valueOf(price));
        order.setTimestamp(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
    }

    @Override
    public List<Order> getAllOrdersOfUser(Long userId, OrderType orderType, String assetSymbol) {
        List<Order> orders = orderRepository.findByUserId(userId);

        if (orderType != null) {
            orders = orders.stream()
                    .filter(order -> order.getOrderType().equals(orderType))
                    .collect(Collectors.toList());
        }

        if (assetSymbol != null && !assetSymbol.isEmpty()) {
            orders = orders.stream()
                    .filter(order -> order.getOrderItem() != null &&
                            order.getOrderItem().getCoin().getSymbol().equalsIgnoreCase(assetSymbol))
                    .collect(Collectors.toList());
        }

        return orders;
    }

    private OrderItem createOrderItem(Coin coin, double quantity, double buyPrice, double sellPrice) {
        OrderItem orderItem = new OrderItem();
        orderItem.setCoin(coin);
        orderItem.setQuantity(quantity);
        orderItem.setBuyPrice(buyPrice);
        orderItem.setSellPrice(sellPrice);
        return orderItemRepository.save(orderItem);
    }

    @Override
    @Transactional
    public Order processOrder(Coin coin, double quantity, OrderType orderType, User user) throws Exception {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        if (orderType.equals(OrderType.BUY)) {
            return buyAsset(coin, quantity, user);
        } else if (orderType.equals(OrderType.SELL)) {
            return sellAsset(coin, quantity, user);
        }

        throw new IllegalArgumentException("Unsupported order type");
    }

    @Transactional
    public Order buyAsset(Coin coin, double quantity, User user) throws Exception {
        double totalCost = coin.getCurrentPrice() * quantity;

        Wallet wallet = walletService.getUserWallet(user);
        if (wallet.getBalance().compareTo(BigDecimal.valueOf(totalCost)) < 0) {
            throw new IllegalArgumentException("Insufficient wallet balance to buy " + coin.getName());
        }

        OrderItem orderItem = createOrderItem(coin, quantity, coin.getCurrentPrice(), 0);
        Order order = createOrder(user, orderItem, OrderType.BUY);
        orderItem.setOrder(order);

        // Deduct money from wallet
        walletService.payOrderPayment(order, user);

        order.setStatus(OrderStatus.SUCCESS);
        Order savedOrder = orderRepository.save(order);

        // Update or create Asset holding
        Asset asset = assetRepository.findByUserIdAndCoinId(user.getId(), coin.getId()).orElse(null);

        if (asset == null) {
            Asset newAsset = new Asset();
            newAsset.setUser(user);
            newAsset.setCoin(coin);
            newAsset.setQuantity(quantity);
            newAsset.setBuyPrice(coin.getCurrentPrice());
            assetRepository.save(newAsset);
        } else {
            asset.setQuantity(asset.getQuantity() + quantity);
            assetRepository.save(asset);
        }

        return savedOrder;
    }

    @Transactional
    public Order sellAsset(Coin coin, double quantity, User user) throws Exception {
        Asset asset = assetRepository.findByUserIdAndCoinId(user.getId(), coin.getId())
                .orElseThrow(() -> new IllegalArgumentException("You do not own any " + coin.getName() + " to sell"));

        if (asset.getQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient coin balance to sell. Available: " + asset.getQuantity());
        }

        OrderItem orderItem = createOrderItem(coin, quantity, asset.getBuyPrice(), coin.getCurrentPrice());
        Order order = createOrder(user, orderItem, OrderType.SELL);
        orderItem.setOrder(order);

        // Credit money to wallet
        walletService.payOrderPayment(order, user);

        order.setStatus(OrderStatus.SUCCESS);
        Order savedOrder = orderRepository.save(order);

        // Deduct asset quantity or remove if zero
        double updatedQuantity = asset.getQuantity() - quantity;
        if (updatedQuantity <= 0) {
            assetRepository.delete(asset);
        } else {
            asset.setQuantity(updatedQuantity);
            assetRepository.save(asset);
        }

        return savedOrder;
    }
}