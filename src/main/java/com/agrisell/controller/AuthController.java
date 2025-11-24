package com.agrisell.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agrisell.dto.AddressDTO;
import com.agrisell.dto.LoginReq;
import com.agrisell.dto.LoginResponseDTO;
import com.agrisell.dto.UserDTO;
import com.agrisell.dto.UserRegistetionDTO;
import com.agrisell.security.JwtUtil;
import com.agrisell.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    // ✅ Register Farmer
    @PostMapping("/register/farmer")
    public ResponseEntity<LoginResponseDTO> registerFarmer(@RequestBody UserRegistetionDTO dto) {
        return ResponseEntity.ok(authService.registerFarmer(dto));
    }

    // ✅ Register Buyer
    @PostMapping("/register/buyer")
    public ResponseEntity<LoginResponseDTO> registerBuyer(@RequestBody UserRegistetionDTO dto) {
        return ResponseEntity.ok(authService.registerBuyer(dto));
    }

    // ✅ Login
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginReq loginReq) {
        return ResponseEntity.ok(authService.login(loginReq));
    }


}
