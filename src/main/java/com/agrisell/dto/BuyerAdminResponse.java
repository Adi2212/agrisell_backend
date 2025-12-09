package com.agrisell.dto;

import com.agrisell.model.AccStatus;
import lombok.Data;

@Data
public class BuyerAdminResponse {
    private Long id;
    private String name;
    private String email;
    private int orderCount;
    private double totalSpent;
    private AccStatus accStatus;
}

