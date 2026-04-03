package com.exelyent.task.controller;


import com.exelyent.task.dto.PaymentDto;
import com.exelyent.task.security.CustomUserDetails;
import com.exelyent.task.security.StripeConfig;
import com.exelyent.task.services.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.stripe.model.Event;
import com.stripe.net.Webhook;

import java.util.List;

@RestController
@RequestMapping
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;
    private final StripeConfig stripeConfig;

    public PaymentController(PaymentService paymentService, StripeConfig stripeConfig) {
        this.paymentService = paymentService;
        this.stripeConfig = stripeConfig;
    }


    @PostMapping("/payments/create-intent")
    public ResponseEntity<PaymentDto.IntentResponse> createPaymentIntent(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PaymentDto.CreateRequest request) {

        PaymentDto.IntentResponse response =
                paymentService.createPaymentIntent(
                        userDetails.getId(), request.getOrderId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/payments/confirm")
    public ResponseEntity<PaymentDto.PaymentResponse> confirmPayment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PaymentDto.ConfirmRequest request) {

        PaymentDto.PaymentResponse response =
                paymentService.confirmPayment(userDetails.getId(), request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/stripe/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        try {
            Event event = Webhook.constructEvent(
                    payload,
                    sigHeader,
                    stripeConfig.getWebhookSecret()
            );

            System.out.println("✅ Event received: " + event.getType());

            // ✅ HANDLE EVENTS
            switch (event.getType()) {

                case "payment_intent.succeeded":

                    var paymentIntent = (com.stripe.model.PaymentIntent)
                            event.getDataObjectDeserializer().getObject().get();

                    String paymentId = paymentIntent.getId();
                    Long amount = paymentIntent.getAmount();

                    System.out.println("💰 Payment Success: " + paymentId);
                    System.out.println("💰 Amount: " + amount);

                    // 🔥 TODO: update order status in DB
                    // paymentService.markPaymentSuccess(paymentId);

                    break;


                case "payment_intent.payment_failed":

                    var failedIntent = (com.stripe.model.PaymentIntent)
                            event.getDataObjectDeserializer().getObject().get();

                    System.out.println("❌ Payment Failed: " + failedIntent.getId());

               

                    break;
            }

            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Webhook Error");
        }
    }

   
    @GetMapping("/payments/order/{orderId}")
    public ResponseEntity<PaymentDto.PaymentResponse> getPaymentByOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long orderId) {

        PaymentDto.PaymentResponse response =
                paymentService.getPaymentByOrderId(userDetails.getId(), orderId);
        return ResponseEntity.ok(response);
    }

  
    @GetMapping("/payments/history")
    public ResponseEntity<List<PaymentDto.PaymentResponse>> getPaymentHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<PaymentDto.PaymentResponse> history =
                paymentService.getPaymentHistory(userDetails.getId());
        return ResponseEntity.ok(history);
    }
}