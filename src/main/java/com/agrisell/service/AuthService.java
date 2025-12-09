package com.agrisell.service;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agrisell.dto.AddressDTO;
import com.agrisell.dto.LoginReq;
import com.agrisell.dto.LoginResponseDTO;
import com.agrisell.dto.UserDTO;
import com.agrisell.dto.UserRegistetionDTO;
import com.agrisell.exception.UserNotFound;
import com.agrisell.model.Address;
import com.agrisell.model.Role;
import com.agrisell.model.User;
import com.agrisell.repository.UserRepository;
import com.agrisell.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final ModelMapper mapper;

    // ✅ FARMER REGISTRATION
    public LoginResponseDTO registerFarmer(UserRegistetionDTO dto) {
        if (userRepo.findByEmail(dto.getEmail()).isPresent())
            throw new IllegalArgumentException("Email already exists");

        User user = mapper.map(dto, User.class);
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setRole(Role.FARMER);


        if (user.getAddress() != null)
            user.getAddress().setUser(user);

        userRepo.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().toString());
        UserDTO userDTO = mapper.map(user, UserDTO.class);
        return new LoginResponseDTO(token, "Farmer registered successfully", userDTO);
    }

    // ✅ CUSTOMER REGISTRATION
    public LoginResponseDTO registerBuyer(UserRegistetionDTO dto) {
        if (userRepo.findByEmail(dto.getEmail()).isPresent())
            throw new IllegalArgumentException("Email already exists");

        User user = mapper.map(dto, User.class);
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setRole(Role.BUYER);


        if (user.getAddress() != null)
            user.getAddress().setUser(user);

        userRepo.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().toString());
        UserDTO userDTO = mapper.map(user, UserDTO.class);
        return new LoginResponseDTO(token, "Buyer registered successfully", userDTO);
    }

    // ✅ LOGIN
    public LoginResponseDTO login(LoginReq loginReq) {
    	
        User user = userRepo.findByEmail(loginReq.getEmail())
                .orElseThrow(() -> new UserNotFound("Invalid email or user not found"));

        if (!encoder.matches(loginReq.getPassword(), user.getPassword()))
            throw new IllegalArgumentException("Invalid password");

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().toString());
        UserDTO userDTO = mapper.map(user, UserDTO.class);

        return new LoginResponseDTO(token, "Login successful", userDTO);
    }


}
