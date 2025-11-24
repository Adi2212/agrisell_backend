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
public class AuthService {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final ModelMapper mapper;

    // ✅ FARMER REGISTRATION
    @Transactional
    public LoginResponseDTO registerFarmer(UserRegistetionDTO dto) {
        if (userRepo.findByEmail(dto.getEmail()).isPresent())
            throw new IllegalArgumentException("Email already exists");

        User user = mapper.map(dto, User.class);
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setRole(Role.FARMER);

        if (user.getFarmer() != null)
            user.getFarmer().setUser(user);
        if (user.getAddress() != null)
            user.getAddress().setUser(user);

        userRepo.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().toString());
        UserDTO userDTO = mapper.map(user, UserDTO.class);
        return new LoginResponseDTO(token, "Farmer registered successfully", userDTO);
    }

    // ✅ CUSTOMER REGISTRATION
    @Transactional
    public LoginResponseDTO registerBuyer(UserRegistetionDTO dto) {
        if (userRepo.findByEmail(dto.getEmail()).isPresent())
            throw new IllegalArgumentException("Email already exists");

        User user = mapper.map(dto, User.class);
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setRole(Role.BUYER);

        if (user.getCustomer() != null)
            user.getCustomer().setUser(user);
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

    // ✅ GET USER BY ID
    public UserDTO getUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFound("User not found"));
        return mapper.map(user, UserDTO.class);
    }

    // ✅ UPDATE ADDRESS (secured)
    @Transactional
    public UserDTO setUserAddress(Long userId, AddressDTO addressDTO) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFound("User not found"));

        Address address = user.getAddress();
        if (address == null) {
            address = new Address();
            address.setUser(user);
        }

        mapper.map(addressDTO, address);
        user.setAddress(address);
        userRepo.save(user);

        return mapper.map(user, UserDTO.class);
    }
}
