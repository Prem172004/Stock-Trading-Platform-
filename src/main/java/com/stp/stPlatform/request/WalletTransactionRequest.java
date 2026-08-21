package com.stp.stPlatform.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletTransactionRequest {
    private Long walletId;
    private BigDecimal amount;
    private String purpose;
}