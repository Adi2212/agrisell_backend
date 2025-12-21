package com.agrisell.dto;

import com.agrisell.model.PaymentStatus;
import com.agrisell.model.Status;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {

    private Long orderId;
    private Double totalAmount;

    private Status orderStatus;
    private PaymentStatus paymentStatus;

    private String paymentMethod;

    private AddressResponse deliveryAddress;

    private List<OrderItemResponse> items;

    private LocalDateTime createdAt;
}
