package com.exelyent.task.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDto {

    public static class CreateRequest {
        private Long orderId;

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
    }

    public static class ConfirmRequest {
        private Long orderId;
        private String paymentIntentId;

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }

        public String getPaymentIntentId() { return paymentIntentId; }
        public void setPaymentIntentId(String paymentIntentId) {
            this.paymentIntentId = paymentIntentId;
        }
    }

    public static class IntentResponse {
        private String clientSecret;      
        private String paymentIntentId;
        private BigDecimal amount;
        private String currency;
        private String status;
        private Long orderId;
        private String orderNumber;

        public String getClientSecret() { return clientSecret; }
        public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }

        public String getPaymentIntentId() { return paymentIntentId; }
        public void setPaymentIntentId(String paymentIntentId) {
            this.paymentIntentId = paymentIntentId;
        }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }

        public String getOrderNumber() { return orderNumber; }
        public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    }

    public static class PaymentResponse {
        private Long id;
        private String stripePaymentIntentId;
        private Long orderId;
        private String orderNumber;
        private BigDecimal amount;
        private String currency;
        private String status;
        private String failureReason;
        private LocalDateTime paidAt;
        private LocalDateTime createdAt;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getStripePaymentIntentId() { return stripePaymentIntentId; }
        public void setStripePaymentIntentId(String s) { this.stripePaymentIntentId = s; }

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }

        public String getOrderNumber() { return orderNumber; }
        public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getFailureReason() { return failureReason; }
        public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

        public LocalDateTime getPaidAt() { return paidAt; }
        public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}