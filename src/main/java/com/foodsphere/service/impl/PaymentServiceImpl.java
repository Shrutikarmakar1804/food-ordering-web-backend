package com.foodsphere.service.impl;

import com.foodsphere.model.Order;
import com.foodsphere.response.PaymentResponse;
import com.foodsphere.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${stripe.secretKey}")
    private String stripeSecretKey;

    @Value("${stripe.currency}")
    private String currency;

    @Value("${stripe.successUrl}")
    private String successUrl;

    @Value("${stripe.cancelUrl}")
    private String cancelUrl;

    @Override
    public PaymentResponse createPaymentLink(Order order) throws StripeException {

        Stripe.apiKey = stripeSecretKey;

        // Convert amount from dollars to cents (or from rupees to paise)
        long amountInCents = (long) (order.getTotalPrice() * 100);

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl + order.getId())
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(currency)
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Order #" + order.getId())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();


        try {
            log.info("Creating Stripe session for order id: {}", order.getId());
            Session session = Session.create(params);

            PaymentResponse response = new PaymentResponse();
            response.setPaymentUrl(session.getUrl());
            log.info("Stripe session created successfully. Payment URL: {}", session.getUrl());
            return response;
        } catch (StripeException e) {
            log.error("Stripe session creation failed for order id: {}", order.getId(), e);
            throw e;
        }
    }
}
