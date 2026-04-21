package com.pharmaorder.loyaltyservice.controller;

import com.pharmaorder.loyaltyservice.model.entity.Loyalty;
import com.pharmaorder.loyaltyservice.repository.LoyaltyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loyalty")
public class LoyaltyController {

    private final LoyaltyRepository loyaltyRepository;

    public LoyaltyController(LoyaltyRepository loyaltyRepository) {
        this.loyaltyRepository = loyaltyRepository;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Loyalty> getLoyaltyByUserId(@PathVariable UUID userId) {
        return loyaltyRepository.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    Loyalty l = new Loyalty();
                    l.setUserId(userId);
                    l.setTotalPoints(150); // Default to signup bonus if account is missing
                    loyaltyRepository.save(l); // Automatically setup the account
                    return ResponseEntity.ok(l);
                });
    }

    /** Called once after account creation. Awards 150 signup health points. */
    @PostMapping("/initialize/{userId}")
    public ResponseEntity<Loyalty> initializeLoyalty(@PathVariable UUID userId) {
        Loyalty loyalty = loyaltyRepository.findByUserId(userId).orElse(null);
        if (loyalty == null) {
            loyalty = new Loyalty();
            loyalty.setUserId(userId);
            loyalty.setTotalPoints(150); // signup bonus
            loyaltyRepository.save(loyalty);
            System.out.println("Awarded 150 signup health points to user " + userId);
        }
        return ResponseEntity.ok(loyalty);
    }
}
