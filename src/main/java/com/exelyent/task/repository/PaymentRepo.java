package com.exelyent.task.repository;

import com.exelyent.task.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {

    Optional<Payment> findByStripePaymentIntentId(String paymentIntentId);

    Optional<Payment> findByOrderId(Long orderId);

    List<Payment> findByUserId(Long userId);

    Optional<Payment> findByOrderIdAndUserId(Long orderId, Long userId);
}