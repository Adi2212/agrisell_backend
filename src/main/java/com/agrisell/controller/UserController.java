package com.agrisell.controller;

import com.agrisell.dto.AddressDTO;
import com.agrisell.dto.UserDTO;
import com.agrisell.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
class UserController {
private final UserService userService;

    // ✅ Get User by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // ✅ Secure endpoint: Update Address
    @PostMapping("/address")
    public ResponseEntity<UserDTO> setUsersAddress(@RequestBody AddressDTO addressDTO, HttpServletRequest request) {
        return ResponseEntity.ok(userService.setUserAddress(addressDTO,request));
    }
}
