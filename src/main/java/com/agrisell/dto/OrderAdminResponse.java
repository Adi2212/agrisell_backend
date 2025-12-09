package com.agrisell.dto;

import lombok.Data;

@Data
public class OrderAdminResponse {
    private Long id;
    private String buyerName;
    private double totalAmount;
    private String status;
    private String createdAt;
}

