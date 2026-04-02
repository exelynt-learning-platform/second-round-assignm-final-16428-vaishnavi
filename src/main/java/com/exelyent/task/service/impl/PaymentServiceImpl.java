package com.exelyent.task.service.impl;


import com.exelyent.task.dto.PaymentDto;
import com.exelyent.task.entity.Order;
import com.exelyent.task.entity.Payment;
import com.exelyent.task.entity.PaymentStatus;
import com.exelyent.task.entity.User;
import com.exelyent.task.exception.BusinessException;
import com.exelyent.task.exception.PaymentException;
import com.exelyent.task.exception.ResourceNotFoundException;
import com.exelyent.task.repository.OrderRepo;
import com.exelyent.task.repository.PaymentRepo;
import com.exelyent.task.repository.UserRepo;
import com.exelyent.task.services.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @Value("${stripe.currency}")
    private String currency;

    private final PaymentRepo paymentRepo;
    private final OrderRepo orderRepo;
    private final UserRepo userRepo;

    public PaymentServiceImpl(PaymentRepo paymentRepo,
                              OrderRepo orderRepo,
                              UserRepo userRepo) {
        this.paymentRepo = paymentRepo;
        this.orderRepo   = orderRepo;
        this.userRepo    = userRepo;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
        log.info("Stripe initialized successfully");
    }


    @Override
    @Transactional
    public PaymentDto.IntentResponse createPaymentIntent(Long userId, Long orderId) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new BusinessException("Order does not belong to this user");
        }

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new BusinessException(
                    "Order is not in PENDING state. Current status: " + order.getStatus());
        }

        paymentRepo.findByOrderId(orderId).ifPresent(existing -> {
            if (existing.getStatus() == PaymentStatus.COMPLETED) {
                throw new BusinessException("Order has already been paid");
            }
        });

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        try {
           
            long amountInCents = order.getGrandTotal()
                    .multiply(BigDecimal.valueOf(100))
                    .longValue();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(currency)
                    .setDescription("Order #" + order.getOrderNumber())
                    .putMetadata("orderId",     String.valueOf(order.getId()))
                    .putMetadata("orderNumber", order.getOrderNumber())
                    .putMetadata("userId",      String.valueOf(userId))
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            Payment payment = new Payment();
            payment.setStripePaymentIntentId(paymentIntent.getId());
            payment.setClientSecret(paymentIntent.getClientSecret());
            payment.setOrder(order);
            payment.setUser(user);
            payment.setAmount(order.getGrandTotal());
            payment.setCurrency(currency);
            payment.setStatus(PaymentStatus.PENDING);
            paymentRepo.save(payment);

            order.setStripePaymentIntentId(paymentIntent.getId());
            order.setPaymentStatus(Order.PaymentStatus.PROCESSING);
            orderRepo.save(order);

            log.info("PaymentIntent created: {} for order: {}",
                    paymentIntent.getId(), order.getOrderNumber());

            PaymentDto.IntentResponse response = new PaymentDto.IntentResponse();
            response.setClientSecret(paymentIntent.getClientSecret());
            response.setPaymentIntentId(paymentIntent.getId());
            response.setAmount(order.getGrandTotal());
            response.setCurrency(currency);
            response.setStatus(paymentIntent.getStatus());
            response.setOrderId(order.getId());
            response.setOrderNumber(order.getOrderNumber());
            return response;

        } catch (StripeException e) {
            log.error("Stripe error creating PaymentIntent: {}", e.getMessage());
            throw new PaymentException("Failed to create payment intent: " + e.getMessage(), e);
        }
    }


    @Override
    @Transactional
    public PaymentDto.PaymentResponse confirmPayment(Long userId,
                                                      PaymentDto.ConfirmRequest request) {
        Payment payment = paymentRepo
                .findByStripePaymentIntentId(request.getPaymentIntentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment", "paymentIntentId", request.getPaymentIntentId()));

        if (!payment.getUser().getId().equals(userId)) {
            throw new BusinessException("Payment does not belong to this user");
        }

        try {
            // Retrieve latest status from Stripe
            PaymentIntent paymentIntent =
                    PaymentIntent.retrieve(request.getPaymentIntentId());

            String stripeStatus = paymentIntent.getStatus();

            if ("succeeded".equals(stripeStatus)) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setPaidAt(LocalDateTime.now());

                // Extract charge ID if available
                if (paymentIntent.getLatestCharge() != null) {
                    payment.setStripeChargeId(paymentIntent.getLatestCharge());
                }

                // Update order
                Order order = payment.getOrder();
                order.setPaymentStatus(Order.PaymentStatus.PAID);
                order.setStatus(Order.OrderStatus.CONFIRMED);
                order.setPaidAt(LocalDateTime.now());
                orderRepo.save(order);

                log.info("Payment confirmed for order: {}", order.getOrderNumber());

            } else if ("requires_payment_method".equals(stripeStatus)
                    || "canceled".equals(stripeStatus)) {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailureReason("Payment failed with status: " + stripeStatus);

                Order order = payment.getOrder();
                order.setPaymentStatus(Order.PaymentStatus.FAILED);
                orderRepo.save(order);
            }

            paymentRepo.save(payment);
            return mapToResponse(payment);

        } catch (StripeException e) {
            log.error("Stripe error confirming payment: {}", e.getMessage());
            throw new PaymentException("Failed to confirm payment: " + e.getMessage(), e);
        }
    }


    @Override
    @Transactional
    public void handleWebhook(String payload, String sigHeader) {
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.error("Invalid Stripe webhook signature: {}", e.getMessage());
            throw new PaymentException("Invalid webhook signature");
        }

        log.info("Stripe webhook received: {}", event.getType());

        EventDataObjectDeserializer dataObjectDeserializer =
                event.getDataObjectDeserializer();

        StripeObject stripeObject = dataObjectDeserializer.getObject()
                .orElseThrow(() -> new PaymentException("Could not deserialize Stripe event"));

        switch (event.getType()) {

            case "payment_intent.succeeded" -> {
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                handlePaymentSucceeded(paymentIntent);
            }

            case "payment_intent.payment_failed" -> {
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                handlePaymentFailed(paymentIntent);
            }

            case "payment_intent.canceled" -> {
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                handlePaymentCancelled(paymentIntent);
            }

            default -> log.info("Unhandled webhook event type: {}", event.getType());
        }
    }


    private void handlePaymentSucceeded(PaymentIntent paymentIntent) {
        paymentRepo.findByStripePaymentIntentId(paymentIntent.getId())
                .ifPresent(payment -> {
                    if (payment.getStatus() == PaymentStatus.COMPLETED) {
                        log.info("Payment already processed: {}", paymentIntent.getId());
                        return; 
                    }

                    payment.setStatus(PaymentStatus.COMPLETED);
                    payment.setPaidAt(LocalDateTime.now());

                    if (paymentIntent.getLatestCharge() != null) {
                        payment.setStripeChargeId(paymentIntent.getLatestCharge());
                    }

                    Order order = payment.getOrder();
                    order.setPaymentStatus(Order.PaymentStatus.PAID);
                    order.setStatus(Order.OrderStatus.CONFIRMED);
                    order.setPaidAt(LocalDateTime.now());
                    orderRepo.save(order);
                    paymentRepo.save(payment);

                    log.info("Webhook: payment succeeded for order: {}",
                            order.getOrderNumber());
                });
    }

    private void handlePaymentFailed(PaymentIntent paymentIntent) {
        paymentRepo.findByStripePaymentIntentId(paymentIntent.getId())
                .ifPresent(payment -> {
                    String reason = "Payment failed";
                    if (paymentIntent.getLastPaymentError() != null) {
                        reason = paymentIntent.getLastPaymentError().getMessage();
                    }

                    payment.setStatus(PaymentStatus.FAILED);
                    payment.setFailureReason(reason);

                    Order order = payment.getOrder();
                    order.setPaymentStatus(Order.PaymentStatus.FAILED);
                    orderRepo.save(order);
                    paymentRepo.save(payment);

                    log.warn("Webhook: payment failed for order: {} — reason: {}",
                            order.getOrderNumber(), reason);
                });
    }

    private void handlePaymentCancelled(PaymentIntent paymentIntent) {
        paymentRepo.findByStripePaymentIntentId(paymentIntent.getId())
                .ifPresent(payment -> {
                    payment.setStatus(PaymentStatus.CANCELLED);

                    Order order = payment.getOrder();
                    order.setPaymentStatus(Order.PaymentStatus.FAILED);
                    orderRepo.save(order);
                    paymentRepo.save(payment);

                    log.info("Webhook: payment cancelled for order: {}",
                            order.getOrderNumber());
                });
    }


    @Override
    @Transactional(readOnly = true)
    public PaymentDto.PaymentResponse getPaymentByOrderId(Long userId, Long orderId) {
        Payment payment = paymentRepo.findByOrderIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment", "orderId", orderId));
        return mapToResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto.PaymentResponse> getPaymentHistory(Long userId) {
        return paymentRepo.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    private PaymentDto.PaymentResponse mapToResponse(Payment payment) {
        PaymentDto.PaymentResponse response = new PaymentDto.PaymentResponse();
        response.setId(payment.getId());
        response.setStripePaymentIntentId(payment.getStripePaymentIntentId());
        response.setOrderId(payment.getOrder().getId());
        response.setOrderNumber(payment.getOrder().getOrderNumber());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setStatus(payment.getStatus().name());
        response.setFailureReason(payment.getFailureReason());
        response.setPaidAt(payment.getPaidAt());
        response.setCreatedAt(payment.getCreatedAt());
        return response;
    }
}