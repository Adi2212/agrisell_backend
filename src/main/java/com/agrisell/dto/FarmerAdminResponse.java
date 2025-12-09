package com.agrisell.dto;

import com.agrisell.model.AccStatus;
import lombok.Data;

@Data
public class FarmerAdminResponse {
    private Long id;
    private String name;
    private String phone;
    private int productCount;
    private AccStatus accStatus;
}

