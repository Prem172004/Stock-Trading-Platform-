package com.stp.stPlatform.controller;

import com.stp.stPlatform.model.PaymentDetails;
import com.stp.stPlatform.model.User;
import com.stp.stPlatform.service.PaymentDetailsService;
import com.stp.stPlatform.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment-details")
public class PaymentDetailsController {

    private final UserService userService;
    private final PaymentDetailsService paymentDetailsService;

    public PaymentDetailsController(UserService userService, PaymentDetailsService paymentDetailsService) {
        this.userService = userService;
        this.paymentDetailsService = paymentDetailsService;
    }

    @PostMapping
    public ResponseEntity<PaymentDetails> addPaymentDetails(
            @RequestBody PaymentDetails paymentDetailsRequest,
            @RequestHeader("Authorization") String jwt) {

        User user = userService.findUserProfileByJwt(jwt);

        PaymentDetails paymentDetails = paymentDetailsService.addPaymentDetails(
                paymentDetailsRequest.getAccountNumber(),
                paymentDetailsRequest.getAccountHolderName(),
                paymentDetailsRequest.getIfsc(),
                paymentDetailsRequest.getBankName(),
                user
        );

        return new ResponseEntity<>(paymentDetails, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PaymentDetails> getUsersPaymentDetails(
            @RequestHeader("Authorization") String jwt) {

        User user = userService.findUserProfileByJwt(jwt);
        PaymentDetails paymentDetails = paymentDetailsService.getUsersPaymentDetails(user);

        return new ResponseEntity<>(paymentDetails, HttpStatus.OK);
    }
}