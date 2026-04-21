package com.pharmaorder.userservice.controller;

import com.pharmaorder.userservice.model.entity.User;
import com.pharmaorder.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<com.pharmaorder.userservice.model.dto.UserDto> getMe() {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .map(user -> {
                    com.pharmaorder.userservice.model.dto.UserDto dto = com.pharmaorder.userservice.model.dto.UserDto.builder()
                            .id(user.getId().toString())
                            .email(user.getEmail())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .phone(user.getPhone())
                            .address(user.getAddress())
                            .roles(user.getRoles().stream().map(com.pharmaorder.userservice.model.entity.Role::getName).collect(java.util.stream.Collectors.toSet()))
                            .build();
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @org.springframework.web.bind.annotation.PutMapping("/me")
    public ResponseEntity<com.pharmaorder.userservice.model.dto.UserDto> updateMe(@org.springframework.web.bind.annotation.RequestBody com.pharmaorder.userservice.model.dto.UserDto updates) {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .map(user -> {
                    if (updates.getFirstName() != null) user.setFirstName(updates.getFirstName());
                    if (updates.getLastName() != null) user.setLastName(updates.getLastName());
                    if (updates.getPhone() != null) user.setPhone(updates.getPhone());
                    if (updates.getAddress() != null) user.setAddress(updates.getAddress());
                    
                    User saved = userRepository.save(user);
                    
                    com.pharmaorder.userservice.model.dto.UserDto dto = com.pharmaorder.userservice.model.dto.UserDto.builder()
                            .id(saved.getId().toString())
                            .email(saved.getEmail())
                            .firstName(saved.getFirstName())
                            .lastName(saved.getLastName())
                            .phone(saved.getPhone())
                            .address(saved.getAddress())
                            .roles(saved.getRoles().stream().map(com.pharmaorder.userservice.model.entity.Role::getName).collect(java.util.stream.Collectors.toSet()))
                            .build();
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
