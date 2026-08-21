package com.stp.stPlatform.controller;

import com.stp.stPlatform.model.Order;
import com.stp.stPlatform.model.User;
import com.stp.stPlatform.model.Wallet;
import com.stp.stPlatform.request.WalletTransactionRequest;
import com.stp.stPlatform.service.OrderService;
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

    public WalletController(WalletService walletService,
                            UserService userService,
                            OrderService orderService) {
        this.walletService = walletService;
        this.userService = userService;
        this.orderService = orderService;
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
            @RequestParam(name = "order_id", required = false) Long orderId,
            @RequestParam(name = "payment_id", required = false) String paymentId,
            @RequestParam BigDecimal amount) {

        User user = userService.findUserProfileByJwt(jwt);
        Wallet wallet = walletService.getUserWallet(user);

        Wallet updatedWallet = walletService.addBalance(wallet, amount);
        return new ResponseEntity<>(updatedWallet, HttpStatus.ACCEPTED);
    }
}