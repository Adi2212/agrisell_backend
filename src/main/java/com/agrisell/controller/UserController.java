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

    //Get User by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // Update Personal Info (name, phone)
    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(
            @RequestBody UserDTO userDTO,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(userService.updateProfile(userDTO, request));
    }

    //Update Address
    @PutMapping("/address")
    public ResponseEntity<UserDTO> setUsersAddress(
            @RequestBody AddressDTO addressDTO,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(userService.setUserAddress(addressDTO, request));
    }

    //Update Profile Photo
    @PutMapping("/profile-photo")
    public ResponseEntity<UserDTO> saveProfilePhoto(
            @RequestBody UserDTO userDTO,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(userService.updateProfilePhoto(userDTO, request));
    }
}
