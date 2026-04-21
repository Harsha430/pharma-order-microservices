package com.pharmaorder.userservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@NoArgsConstructor @AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    // Manual Builder
    public static RoleBuilder builder() { return new RoleBuilder(); }
    public static class RoleBuilder {
        private Role r = new Role();
        public RoleBuilder name(String name) { r.name = name; return this; }
        public Role build() { return r; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
