package com.stp.stPlatform.service;

import com.stp.stPlatform.domain.WithdrawalStatus;
import com.stp.stPlatform.model.User;
import com.stp.stPlatform.model.Wallet;
import com.stp.stPlatform.model.Withdrawal;
import com.stp.stPlatform.repository.WithdrawalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WithdrawalServiceImpl implements WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;
    private final WalletService walletService;

    public WithdrawalServiceImpl(WithdrawalRepository withdrawalRepository,
                                 WalletService walletService) {
        this.withdrawalRepository = withdrawalRepository;
        this.walletService = walletService;
    }

    @Override
    @Transactional
    public Withdrawal requestWithdrawal(Long amount, User user) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero");
        }

        Wallet userWallet = walletService.getUserWallet(user);
        BigDecimal withdrawBigDecimal = BigDecimal.valueOf(amount);

        if (userWallet.getBalance().compareTo(withdrawBigDecimal) < 0) {
            throw new IllegalArgumentException("Insufficient wallet balance for withdrawal");
        }

        // Deduct from wallet immediately upon request
        userWallet.setBalance(userWallet.getBalance().subtract(withdrawBigDecimal));

        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setAmount(amount);
        withdrawal.setUser(user);
        withdrawal.setStatus(WithdrawalStatus.PENDING);
        withdrawal.setDate(LocalDateTime.now());

        return withdrawalRepository.save(withdrawal);
    }

    @Override
    @Transactional
    public Withdrawal proceedWithdrawal(Long withdrawalId, boolean accept) throws Exception {
        Withdrawal withdrawal = withdrawalRepository.findById(withdrawalId)
                .orElseThrow(() -> new Exception("Withdrawal request not found with id: " + withdrawalId));

        if (!withdrawal.getStatus().equals(WithdrawalStatus.PENDING)) {
            throw new Exception("Withdrawal request is already processed");
        }

        if (accept) {
            withdrawal.setStatus(WithdrawalStatus.SUCCESS);
        } else {
            // Refund amount back to user's wallet if rejected
            Wallet userWallet = walletService.getUserWallet(withdrawal.getUser());
            walletService.addBalance(userWallet, BigDecimal.valueOf(withdrawal.getAmount()));
            withdrawal.setStatus(WithdrawalStatus.DECLINE);
        }

        return withdrawalRepository.save(withdrawal);
    }

    @Override
    public List<Withdrawal> getUsersWithdrawalHistory(User user) {
        return withdrawalRepository.findByUserId(user.getId());
    }

    @Override
    public List<Withdrawal> getAllWithdrawalRequest() {
        return withdrawalRepository.findAll();
    }
}