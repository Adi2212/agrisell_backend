package com.agrisell.service;

import com.agrisell.dto.OrderItemRequest;
import com.agrisell.dto.StripeResponse;
import com.agrisell.model.Product;
import com.agrisell.repository.ProductRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ProductRepository productRepository;

    // 🔐 Loaded from application.yml / env variable
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    // ================= INIT STRIPE =================
    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
        System.out.println("Stripe initialized");
    }

    // ================= STRIPE CHECKOUT =================
    public StripeResponse checkout(
            Long orderId,
            List<OrderItemRequest> items
    ) {

        try {
            SessionCreateParams.Builder params =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl(
                                    "http://localhost:5173/payment/success?orderId=" + orderId
                            )
                            .setCancelUrl(
                                    "http://localhost:5173/payment/cancel?orderId=" + orderId
                            );

            // 🔹 Add all order items
            for (OrderItemRequest item : items) {

                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                SessionCreateParams.LineItem.PriceData.ProductData productData =
                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName(product.getName())
                                .build();

                SessionCreateParams.LineItem.PriceData priceData =
                        SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("inr")
                                .setUnitAmount(
                                        Math.round(product.getPrice() * 100) // ₹ → paise
                                )
                                .setProductData(productData)
                                .build();

                SessionCreateParams.LineItem lineItem =
                        SessionCreateParams.LineItem.builder()
                                .setQuantity((long) item.getQuantity())
                                .setPriceData(priceData)
                                .build();

                params.addLineItem(lineItem);
            }

            // 🔹 Create Stripe session
            Session session = Session.create(params.build());

            return StripeResponse.builder()
                    .sessionId(session.getId())
                    .sessionUrl(session.getUrl())
                    .status("SUCCESS")
                    .build();

        } catch (StripeException e) {
            return StripeResponse.builder()
                    .status("FAILED")
                    .message(e.getMessage())
                    .build();
        }
    }
}
