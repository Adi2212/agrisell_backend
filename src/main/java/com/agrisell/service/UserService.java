package com.agrisell.service;

import com.agrisell.dto.AddressDTO;
import com.agrisell.dto.UserDTO;
import com.agrisell.exception.UserNotFound;
import com.agrisell.model.Address;
import com.agrisell.model.User;
import com.agrisell.repository.UserRepository;
import com.agrisell.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final ModelMapper mapper;
    private final JwtUtil jwtUtil;

    //GET USER BY ID
    public UserDTO getUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFound("User not found"));
        return mapper.map(user, UserDTO.class);
    }


    public UserDTO updateProfile(UserDTO userDTO, HttpServletRequest request) {

        Long userId = jwtUtil.extractUserId(jwtUtil.extractToken(request));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFound("User not found"));


        user.setName(userDTO.getName());
        user.setPhone(userDTO.getPhone());

        User savedUser = userRepo.save(user);
        return mapper.map(savedUser, UserDTO.class);
    }


    public UserDTO setUserAddress(AddressDTO addressDTO, HttpServletRequest request) {

        Long userId = jwtUtil.extractUserId(jwtUtil.extractToken(request));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFound("User not found"));

        Address address = user.getAddress();

        if (address == null) {
            address = new Address();
            address.setUser(user);
        }

        mapper.map(addressDTO, address);
        user.setAddress(address);

        User savedUser = userRepo.save(user);
        return mapper.map(savedUser, UserDTO.class);
    }


    public UserDTO updateProfilePhoto(UserDTO userDTO, HttpServletRequest request) {

        Long userId = jwtUtil.extractUserId(jwtUtil.extractToken(request));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFound("User not found"));

        user.setProfileUrl(userDTO.getProfileUrl());

        User savedUser = userRepo.save(user);
        return mapper.map(savedUser, UserDTO.class);
    }

}
