package com.pharmaorder.loyaltyservice.service;

import com.pharmaorder.loyaltyservice.model.entity.Loyalty;
import com.pharmaorder.loyaltyservice.repository.LoyaltyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
public class LoyaltyListener {

    private final LoyaltyRepository loyaltyRepository;

    public LoyaltyListener(LoyaltyRepository loyaltyRepository) {
        this.loyaltyRepository = loyaltyRepository;
    }

    @RabbitListener(queues = "loyalty.queue")
    @Transactional
    public void handleOrderCreated(Map<String, Object> event) {
        System.out.println("Processing loyalty points for order: " + event.get("orderId"));
        
        try {
            UUID userId = UUID.fromString((String) event.get("userId"));
            Object amountObj = event.get("totalAmount");
            BigDecimal totalAmount = BigDecimal.ZERO;
            
            if (amountObj instanceof Integer) {
                totalAmount = BigDecimal.valueOf((Integer) amountObj);
            } else if (amountObj instanceof Double) {
                totalAmount = BigDecimal.valueOf((Double) amountObj);
            } else if (amountObj instanceof BigDecimal) {
                totalAmount = (BigDecimal) amountObj;
            }

            // 1. Deduct redeemed points
            int redeemed = 0;
            if (event.get("pointsRedeemed") != null) {
                redeemed = (Integer) event.get("pointsRedeemed");
            }

            // 2. Award 1 health point per ₹100 of the final amount actually paid.
            // totalAmount from the order already reflects the post-discount price.
            int pointsToEarn = totalAmount.divide(new BigDecimal("100"), java.math.RoundingMode.DOWN).intValue();

            Loyalty loyalty = loyaltyRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        Loyalty l = new Loyalty();
                        l.setUserId(userId);
                        l.setTotalPoints(0);
                        return l;
                    });

            int newBalance = loyalty.getTotalPoints() - redeemed + pointsToEarn;
            loyalty.setTotalPoints(Math.max(0, newBalance)); // Prevent negative points
            loyaltyRepository.save(loyalty);
            
            System.out.println("Order " + event.get("orderId") + ": Redeemed " + redeemed + ", Earned " + pointsToEarn + ". New balance: " + loyalty.getTotalPoints());
        } catch (Exception e) {
            System.err.println("Failed to process loyalty for order " + event.get("orderId") + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
