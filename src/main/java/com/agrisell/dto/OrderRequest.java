package com.agrisell.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private String paymentMethod;
    private List<OrderItemRequest> items;
}
