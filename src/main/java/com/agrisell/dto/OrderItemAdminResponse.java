package com.agrisell.dto;

import lombok.Data;

@Data
public class OrderItemAdminResponse {

    private Long id;
    private String productName;
    private Long productId;
    private int quantity;
    private Double price;

    private OrderAddressResponse pickUpAddress;
}
