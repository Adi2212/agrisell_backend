package com.agrisell.controller;

import com.agrisell.dto.PaymentRequest;
import com.agrisell.dto.StripeResponse;
import com.agrisell.service.PaymentService;
import com.agrisell.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@AllArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    // ================= CREATE STRIPE CHECKOUT =================

    @PostMapping("/checkout")
    public ResponseEntity<StripeResponse> checkout(
            @RequestBody PaymentRequest request
    ) {
        StripeResponse response = paymentService.checkout(
                request.getOrderId(),
                request.getItems()
        );
        return ResponseEntity.ok(response);
    }


}
