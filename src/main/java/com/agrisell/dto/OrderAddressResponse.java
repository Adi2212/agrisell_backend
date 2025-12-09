package com.agrisell.dto;

import lombok.Data;

@Data
public class OrderAddressResponse {
    private String street;
    private String city;
    private String district;
    private String state;
    private String pinCode;
}
