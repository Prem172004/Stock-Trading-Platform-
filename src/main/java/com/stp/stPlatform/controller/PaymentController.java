package com.stp.stPlatform.controller;

import com.stp.stPlatform.domain.PaymentMethod;
import com.stp.stPlatform.model.PaymentOrder;
import com.stp.stPlatform.model.User;
import com.stp.stPlatform.response.PaymentResponse;
import com.stp.stPlatform.service.PaymentOrderService;
import com.stp.stPlatform.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final UserService userService;
    private final PaymentOrderService paymentOrderService;

    public PaymentController(UserService userService, PaymentOrderService paymentOrderService) {
        this.userService = userService;
        this.paymentOrderService = paymentOrderService;
    }

    @PostMapping("/{paymentMethod}/amount/{amount}")
    public ResponseEntity<PaymentResponse> paymentHandler(
            @PathVariable PaymentMethod paymentMethod,
            @PathVariable Long amount,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserProfileByJwt(jwt);
        PaymentOrder order = paymentOrderService.createOrder(user, amount, paymentMethod);

        PaymentResponse paymentResponse;

        if (paymentMethod.equals(PaymentMethod.RAZORPAY)) {
            paymentResponse = paymentOrderService.createRazorpayPaymentLink(user, amount, order.getId());
        } else {
            throw new IllegalArgumentException("Unsupported payment method: " + paymentMethod);
        }

        return new ResponseEntity<>(paymentResponse, HttpStatus.CREATED);
    }
}