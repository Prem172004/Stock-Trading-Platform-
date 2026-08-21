package com.stp.stPlatform.service;

import com.stp.stPlatform.model.Order;
import com.stp.stPlatform.model.User;
import com.stp.stPlatform.model.Wallet;
import com.stp.stPlatform.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public Wallet getUserWallet(User user) {
        return walletRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Wallet newWallet = new Wallet();
                    newWallet.setUser(user);
                    newWallet.setBalance(BigDecimal.ZERO);
                    return walletRepository.save(newWallet);
                });
    }

    @Override
    @Transactional
    public Wallet addBalance(Wallet wallet, BigDecimal money) {
        if (money == null || money.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount to add must be greater than zero");
        }

        BigDecimal newBalance = wallet.getBalance().add(money);
        wallet.setBalance(newBalance);
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet findWalletById(Long id) {
        return walletRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found with id: " + id));
    }

    @Override
    @Transactional
    public Wallet walletToWalletTransfer(User sender, Wallet receiverWallet, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }

        Wallet senderWallet = getUserWallet(sender);

        if (senderWallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds for transfer");
        }

        if (senderWallet.getId().equals(receiverWallet.getId())) {
            throw new IllegalArgumentException("Cannot transfer funds to the same wallet");
        }

        // Deduct from sender
        BigDecimal senderNewBalance = senderWallet.getBalance().subtract(amount);
        senderWallet.setBalance(senderNewBalance);
        walletRepository.save(senderWallet);

        // Credit to receiver
        BigDecimal receiverNewBalance = receiverWallet.getBalance().add(amount);
        receiverWallet.setBalance(receiverNewBalance);
        walletRepository.save(receiverWallet);

        return senderWallet;
    }

    @Override
    @Transactional
    public Wallet payOrderPayment(Order order, User user) {
        Wallet wallet = getUserWallet(user);

        if (order.getOrderType().name().equals("BUY")) {
            BigDecimal orderTotal = order.getPrice();
            if (wallet.getBalance().compareTo(orderTotal) < 0) {
                throw new IllegalArgumentException("Insufficient balance to execute buy order");
            }
            BigDecimal newBalance = wallet.getBalance().subtract(orderTotal);
            wallet.setBalance(newBalance);
        } else if (order.getOrderType().name().equals("SELL")) {
            BigDecimal orderTotal = order.getPrice();
            BigDecimal newBalance = wallet.getBalance().add(orderTotal);
            wallet.setBalance(newBalance);
        }

        return walletRepository.save(wallet);
    }
}