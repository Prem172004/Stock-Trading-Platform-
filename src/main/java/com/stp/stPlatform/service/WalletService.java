package com.stp.stPlatform.service;

import com.stp.stPlatform.model.Order;
import com.stp.stPlatform.model.User;
import com.stp.stPlatform.model.Wallet;

import java.math.BigDecimal;

public interface WalletService {
    Wallet getUserWallet(User user);
    Wallet addBalance(Wallet wallet, BigDecimal money);
    Wallet findWalletById(Long id);
    Wallet walletToWalletTransfer(User sender, Wallet receiverWallet, BigDecimal amount);
    Wallet payOrderPayment(Order order, User user);
}