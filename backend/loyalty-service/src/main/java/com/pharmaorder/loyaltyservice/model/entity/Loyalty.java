package com.pharmaorder.loyaltyservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "loyalty")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Loyalty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID userId;

    private Integer totalPoints;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public Integer getTotalPoints() { return totalPoints; }
    public void setTotalPoints(Integer tp) { this.totalPoints = tp; }
}
