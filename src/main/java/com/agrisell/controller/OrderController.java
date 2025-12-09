package com.agrisell.controller;

import com.agrisell.dto.OrderRequest;
import com.agrisell.dto.OrderResponse;
import com.agrisell.model.Order;
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

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest dto, HttpServletRequest request) {
        try{
        return ResponseEntity.ok(orderService.placeOrder(dto, request));}
        catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam Status status){
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }

    @GetMapping("/user")
    public ResponseEntity<?> userOrders(HttpServletRequest request){
        return ResponseEntity.ok(orderService.getUserOrders(request));
    }

    @GetMapping("/single/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Long id){
        return ResponseEntity.ok(orderService.getOrder(id));
    }
}
