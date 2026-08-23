package com.stp.stPlatform.controller;

import com.stp.stPlatform.model.User;
import com.stp.stPlatform.model.Withdrawal;
import com.stp.stPlatform.service.UserService;
import com.stp.stPlatform.service.WithdrawalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class WithdrawalController {

    private final WithdrawalService withdrawalService;
    private final UserService userService;

    public WithdrawalController(WithdrawalService withdrawalService, UserService userService) {
        this.withdrawalService = withdrawalService;
        this.userService = userService;
    }

    // User: Request a new withdrawal
    @PostMapping("/api/withdrawal/{amount}")
    public ResponseEntity<Withdrawal> withdrawalRequest(
            @PathVariable Long amount,
            @RequestHeader("Authorization") String jwt) {
        User user = userService.findUserProfileByJwt(jwt);
        Withdrawal withdrawal = withdrawalService.requestWithdrawal(amount, user);
        return new ResponseEntity<>(withdrawal, HttpStatus.OK);
    }

    // Admin: Accept or decline a withdrawal request
    @PatchMapping("/api/admin/withdrawal/{id}/proceed/{accept}")
    public ResponseEntity<Withdrawal> proceedWithdrawal(
            @PathVariable Long id,
            @PathVariable boolean accept) throws Exception {
        Withdrawal withdrawal = withdrawalService.proceedWithdrawal(id, accept);
        return new ResponseEntity<>(withdrawal, HttpStatus.OK);
    }

    // User: Get personal withdrawal history
    @GetMapping("/api/withdrawal")
    public ResponseEntity<List<Withdrawal>> getWithdrawalHistory(
            @RequestHeader("Authorization") String jwt) {
        User user = userService.findUserProfileByJwt(jwt);
        List<Withdrawal> withdrawals = withdrawalService.getUsersWithdrawalHistory(user);
        return new ResponseEntity<>(withdrawals, HttpStatus.OK);
    }

    // Admin: Get all withdrawal requests across the platform
    @GetMapping("/api/admin/withdrawal")
    public ResponseEntity<List<Withdrawal>> getAllWithdrawalRequest() {
        List<Withdrawal> withdrawals = withdrawalService.getAllWithdrawalRequest();
        return new ResponseEntity<>(withdrawals, HttpStatus.OK);
    }
}