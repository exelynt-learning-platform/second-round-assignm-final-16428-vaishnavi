package com.exelyent.task.security;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    public String getWebhookSecret() {
        return webhookSecret;
    }
}