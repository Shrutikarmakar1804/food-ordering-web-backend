package com.foodsphere.controller;

import com.foodsphere.model.Order;
import com.foodsphere.response.PaymentResponse;
import com.foodsphere.service.PaymentService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Endpoint to create a payment link for an order.
     * @param order the order details (should include id and amount)
     * @return PaymentResponse with payment URL
     */
    @PostMapping("/create-link")
    public ResponseEntity<PaymentResponse> createPaymentLink(@RequestBody Order order) throws StripeException {
        PaymentResponse response = paymentService.createPaymentLink(order);
        return ResponseEntity.ok(response);
    }
}