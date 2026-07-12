package com.sneha.moviebooking.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MockPaymentGatewayService {

    public String processPayment(boolean simulateSuccess) {

        if (!simulateSuccess) {
            return null;
        }

        return "TXN-" + UUID.randomUUID().toString().replace("-", "");
    }
}