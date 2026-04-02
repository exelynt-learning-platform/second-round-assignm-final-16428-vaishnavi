package com.exelyent.task.services;


import com.exelyent.task.dto.PaymentDto;

import java.util.List;

public interface PaymentService {

    PaymentDto.IntentResponse createPaymentIntent(Long userId, Long orderId);

    PaymentDto.PaymentResponse confirmPayment(Long userId, PaymentDto.ConfirmRequest request);

    void handleWebhook(String payload, String sigHeader);

    PaymentDto.PaymentResponse getPaymentByOrderId(Long userId, Long orderId);

    List<PaymentDto.PaymentResponse> getPaymentHistory(Long userId);
}