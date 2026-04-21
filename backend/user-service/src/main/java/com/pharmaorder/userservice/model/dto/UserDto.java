package com.pharmaorder.userservice.model.dto;

import java.util.Set;

public class UserDto {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private Set<String> roles;

    public UserDto() {}

    public UserDto(String id, String email, String firstName, String lastName, String phone, String address, Set<String> roles) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
        this.roles = roles;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String email;
        private String firstName;
        private String lastName;
        private String phone;
        private String address;
        private Set<String> roles;

        public Builder id(String id) { this.id = id; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder firstName(String firstName) { this.firstName = firstName; return this; }
        public Builder lastName(String lastName) { this.lastName = lastName; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder roles(Set<String> roles) { this.roles = roles; return this; }

        public UserDto build() {
            return new UserDto(id, email, firstName, lastName, phone, address, roles);
        }
    }
}
