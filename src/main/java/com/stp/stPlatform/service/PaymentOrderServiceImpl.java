package com.stp.stPlatform.service;

import com.stp.stPlatform.domain.PaymentMethod;
import com.stp.stPlatform.domain.PaymentOrderStatus;
import com.stp.stPlatform.model.PaymentOrder;
import com.stp.stPlatform.model.User;
import com.stp.stPlatform.repository.PaymentOrderRepository;
import com.stp.stPlatform.response.PaymentResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentOrderServiceImpl implements PaymentOrderService {

    private final PaymentOrderRepository paymentOrderRepository;

    public PaymentOrderServiceImpl(PaymentOrderRepository paymentOrderRepository) {
        this.paymentOrderRepository = paymentOrderRepository;
    }

    @Override
    public PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod) {
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setUser(user);
        paymentOrder.setAmount(amount);
        paymentOrder.setPaymentMethod(PaymentMethod.RAZORPAY);
        paymentOrder.setStatus(PaymentOrderStatus.PENDING);
        paymentOrder.setCreatedAt(LocalDateTime.now());

        return paymentOrderRepository.save(paymentOrder);
    }

    @Override
    public PaymentOrder getPaymentOrderById(Long id) throws Exception {
        return paymentOrderRepository.findById(id)
                .orElseThrow(() -> new Exception("Payment order not found with ID: " + id));
    }

    @Override
    public Boolean proceedPaymentOrder(PaymentOrder paymentOrder, String paymentId) throws Exception {
        if (paymentOrder.getStatus() == null) {
            paymentOrder.setStatus(PaymentOrderStatus.PENDING);
        }

        if (paymentOrder.getStatus().equals(PaymentOrderStatus.PENDING)) {
            paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
            paymentOrderRepository.save(paymentOrder);
            return true;
        }
        return false;
    }

    @Override
    public PaymentResponse createRazorpayPaymentLink(User user, Long amount, Long orderId) throws Exception {
        // --- MOCK PAYMENT GATEWAY SIMULATION ---
        // Instantly generates a mock transaction ID and redirects to your local success page
        String mockPaymentId = "pay_mock_" + System.currentTimeMillis();
        String mockRedirectUrl = "http://localhost:5173/payment-success?order_id=" + orderId + "&payment_id=" + mockPaymentId;

        PaymentResponse res = new PaymentResponse();
        res.setPayment_url(mockRedirectUrl);
        return res;
    }

    @Override
    public PaymentResponse createStripePaymentLink(User user, Long amount, Long orderId) throws Exception {
        throw new UnsupportedOperationException("Stripe is disabled. Using Mock/Razorpay flow.");
    }
}