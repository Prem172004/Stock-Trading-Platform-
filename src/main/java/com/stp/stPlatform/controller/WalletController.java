package com.stp.stPlatform.controller;

import com.stp.stPlatform.model.Order;
import com.stp.stPlatform.model.PaymentOrder;
import com.stp.stPlatform.model.User;
import com.stp.stPlatform.model.Wallet;
import com.stp.stPlatform.request.WalletTransactionRequest;
import com.stp.stPlatform.service.OrderService;
import com.stp.stPlatform.service.PaymentOrderService;
import com.stp.stPlatform.service.UserService;
import com.stp.stPlatform.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;
    private final UserService userService;
    private final OrderService orderService;
    private final PaymentOrderService paymentOrderService;

    public WalletController(WalletService walletService,
                            UserService userService,
                            OrderService orderService,
                            PaymentOrderService paymentOrderService) {
        this.walletService = walletService;
        this.userService = userService;
        this.orderService = orderService;
        this.paymentOrderService = paymentOrderService;
    }

    @GetMapping
    public ResponseEntity<Wallet> getUserWallet(
            @RequestHeader("Authorization") String jwt) {
        User user = userService.findUserProfileByJwt(jwt);
        Wallet wallet = walletService.getUserWallet(user);
        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }

    @PutMapping("/{walletId}/transfer")
    public ResponseEntity<Wallet> walletToWalletTransfer(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long walletId,
            @RequestBody WalletTransactionRequest req) {

        User senderUser = userService.findUserProfileByJwt(jwt);
        Wallet receiverWallet = walletService.findWalletById(walletId);

        Wallet updatedWallet = walletService.walletToWalletTransfer(
                senderUser,
                receiverWallet,
                req.getAmount()
        );

        return new ResponseEntity<>(updatedWallet, HttpStatus.ACCEPTED);
    }

    @PutMapping("/order/{orderId}/pay")
    public ResponseEntity<Wallet> payOrderPayment(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long orderId) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);
        Order order = orderService.getOrderById(orderId);

        Wallet updatedWallet = walletService.payOrderPayment(order, user);
        return new ResponseEntity<>(updatedWallet, HttpStatus.ACCEPTED);
    }

    @PutMapping("/deposit")
    public ResponseEntity<Wallet> addMoneyToWallet(
            @RequestHeader("Authorization") String jwt,
            @RequestParam(name = "order_id") Long orderId,
            @RequestParam(name = "payment_id") String paymentId) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);
        Wallet wallet = walletService.getUserWallet(user);

        PaymentOrder paymentOrder = paymentOrderService.getPaymentOrderById(orderId);
        Boolean status = paymentOrderService.proceedPaymentOrder(paymentOrder, paymentId);

        if (wallet.getBalance() == null) {
            wallet.setBalance(BigDecimal.ZERO);
        }

        if (status) {
            wallet = walletService.addBalance(wallet, BigDecimal.valueOf(paymentOrder.getAmount()));
        }

        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }
}