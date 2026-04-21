package com.pharmaorder.userservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor @AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;
    private String phone;
    private String address;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    // Manual Builder
    public static UserBuilder builder() { return new UserBuilder(); }
    public static class UserBuilder {
        private User u = new User();
        public UserBuilder email(String email) { u.email = email; return this; }
        public UserBuilder password(String password) { u.password = password; return this; }
        public UserBuilder firstName(String fn) { u.firstName = fn; return this; }
        public UserBuilder lastName(String ln) { u.lastName = ln; return this; }
        public UserBuilder phone(String ph) { u.phone = ph; return this; }
        public UserBuilder address(String addr) { u.address = addr; return this; }
        public UserBuilder roles(Set<Role> roles) { u.roles = roles; return this; }
        public User build() { return u; }
    }

    // Manual Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
}
