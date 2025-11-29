package com.agrisell.dto;

import lombok.Data;

@Data
public class DeliveryAddressRequest {
    private String fullName;
    private String phone;
    private String city;
    private String state;
    private String pincode;
    private String fullAddress;
}

