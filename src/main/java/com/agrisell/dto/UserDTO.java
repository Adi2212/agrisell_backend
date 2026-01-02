package com.agrisell.dto;

import com.agrisell.model.AccStatus;
import com.agrisell.model.Address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String profileUrl;
    private Address address;
    private AccStatus accStatus;
}
