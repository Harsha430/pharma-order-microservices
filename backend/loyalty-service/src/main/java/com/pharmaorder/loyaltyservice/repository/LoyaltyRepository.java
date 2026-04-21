package com.pharmaorder.loyaltyservice.repository;

import com.pharmaorder.loyaltyservice.model.entity.Loyalty;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface LoyaltyRepository extends JpaRepository<Loyalty, Long> {
    Optional<Loyalty> findByUserId(UUID userId);
}
