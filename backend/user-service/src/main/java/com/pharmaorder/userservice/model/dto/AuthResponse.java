package com.pharmaorder.userservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

public class AuthResponse {
    private String token;
    private String email;
    private java.util.UUID id;
    private java.util.Set<String> roles;

    public AuthResponse() {}

    public AuthResponse(String token, String email, java.util.UUID id, java.util.Set<String> roles) {
        this.token = token;
        this.email = email;
        this.id = id;
        this.roles = roles;
    }

    public static AuthResponse builder() {
        return new AuthResponse();
    }
    
    public AuthResponse token(String token) { this.token = token; return this; }
    public AuthResponse email(String email) { this.email = email; return this; }
    public AuthResponse id(java.util.UUID id) { this.id = id; return this; }
    public AuthResponse roles(java.util.Set<String> roles) { this.roles = roles; return this; }
    public AuthResponse build() { return this; }

    // Manual Getters
    public String getToken() { return token; }
    public String getEmail() { return email; }
    public java.util.UUID getId() { return id; }
    public java.util.Set<String> getRoles() { return roles; }

    // Manual Setters
    public void setToken(String token) { this.token = token; }
    public void setEmail(String email) { this.email = email; }
    public void setId(java.util.UUID id) { this.id = id; }
    public void setRoles(java.util.Set<String> roles) { this.roles = roles; }
}
