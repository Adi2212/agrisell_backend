package com.agrisell.dto;

import com.agrisell.model.Address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistetionDTO {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String role;
    private String phone;
    private Address address;
}
