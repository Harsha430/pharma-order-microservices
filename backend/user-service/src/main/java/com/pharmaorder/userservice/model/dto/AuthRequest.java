package com.pharmaorder.userservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    private String email;
    private String password;

    public static AuthRequestBuilder builder() { return new AuthRequestBuilder(); }
    public static class AuthRequestBuilder {
        private AuthRequest r = new AuthRequest();
        public AuthRequestBuilder email(String email) { r.email = email; return this; }
        public AuthRequestBuilder password(String pass) { r.password = pass; return this; }
        public AuthRequest build() { return r; }
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
