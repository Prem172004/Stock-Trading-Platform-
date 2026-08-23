package com.stp.stPlatform.service;

import com.stp.stPlatform.domain.PaymentMethod;
import com.stp.stPlatform.model.PaymentOrder;
import com.stp.stPlatform.model.User;
import com.stp.stPlatform.response.PaymentResponse;

public interface PaymentOrderService {
    PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod);
    PaymentOrder getPaymentOrderById(Long id) throws Exception;
    Boolean proceedPaymentOrder(PaymentOrder paymentOrder, String paymentId) throws Exception;
    PaymentResponse createRazorpayPaymentLink(User user, Long amount, Long orderId) throws Exception;
    PaymentResponse createStripePaymentLink(User user, Long amount, Long orderId) throws Exception;
}