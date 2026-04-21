package com.pharmaorder.userservice.service;

import com.pharmaorder.common.constants.SecurityConstants;
import com.pharmaorder.userservice.model.dto.AuthRequest;
import com.pharmaorder.userservice.model.dto.AuthResponse;
import com.pharmaorder.userservice.model.dto.RegisterRequest;
import com.pharmaorder.userservice.model.entity.Role;
import com.pharmaorder.userservice.model.entity.User;
import com.pharmaorder.userservice.repository.RoleRepository;
import com.pharmaorder.userservice.repository.UserRepository;
import com.pharmaorder.userservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, 
                       PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Role userRole = roleRepository.findByName(SecurityConstants.ROLE_CUSTOMER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(SecurityConstants.ROLE_CUSTOMER).build()));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .roles(Collections.singleton(userRole))
                .build();

        userRepository.save(user);

        return authenticate(AuthRequest.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build());
    }

    public AuthResponse authenticate(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        Set<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("roles", roles);

        String token = jwtUtil.generateToken(user.getEmail(), extraClaims);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .roles(roles)
                .build();
    }
}
