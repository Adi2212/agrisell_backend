package com.agrisell.dto;

import lombok.Data;

@Data
public class ProductAdminResponse {
    private Long id;
    private String name;
    private String farmerName;
    private double price;
    private int stock;
    private boolean approved;
}

