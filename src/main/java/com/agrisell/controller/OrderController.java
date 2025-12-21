package com.agrisell.controller;

import com.agrisell.dto.OrderRequest;
import com.agrisell.dto.OrderResponse;
import com.agrisell.model.Status;
import com.agrisell.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // ✅ Create Order
    @PostMapping("/create")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody OrderRequest dto,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(orderService.placeOrder(dto, request));
    }

    // ✅ Update Order Status (Admin / Seller)
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam Status status
    ) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }

    // ✅ Get Logged-in User Orders
    @GetMapping("/user")
    public ResponseEntity<List<OrderResponse>> userOrders(
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(orderService.getUserOrders(request));
    }

    // ✅ Get Single Order
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    // ✅ Payment Failed (Cancel)
    @PutMapping("/{id}/payment-failed")
    public ResponseEntity<OrderResponse> markPaymentFailed(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(orderService.markPaymentFailed(id));
    }

    // ✅ Payment Success (Webhook / Success Page)
    @PutMapping("/{id}/payment-success")
    public ResponseEntity<OrderResponse> markPaymentSuccess(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(orderService.markPaymentSuccess(id));
    }
}
