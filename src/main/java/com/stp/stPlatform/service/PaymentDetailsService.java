package com.stp.stPlatform.service;

import com.stp.stPlatform.model.PaymentDetails;
import com.stp.stPlatform.model.User;

public interface PaymentDetailsService {
    PaymentDetails addPaymentDetails(String accountNumber,
                                     String accountHolderName,
                                     String ifsc,
                                     String bankName,
                                     User user);

    PaymentDetails getUsersPaymentDetails(User user);
}