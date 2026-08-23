package com.stp.stPlatform.service;

import com.stp.stPlatform.model.User;
import com.stp.stPlatform.model.Withdrawal;

import java.util.List;

public interface WithdrawalService {
    Withdrawal requestWithdrawal(Long amount, User user);
    Withdrawal proceedWithdrawal(Long withdrawalId, boolean accept) throws Exception;
    List<Withdrawal> getUsersWithdrawalHistory(User user);
    List<Withdrawal> getAllWithdrawalRequest();
}