package com.pharmaorder.inventoryservice.service;

import com.pharmaorder.inventoryservice.model.entity.Inventory;
import com.pharmaorder.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class OrderEventListener {

    private final InventoryRepository inventoryRepository;

    public OrderEventListener(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @RabbitListener(queues = "inventory.queue")
    @Transactional
    public void handleOrderCreated(Map<String, Object> event) {
        System.out.println("Processing inventory update for order: " + event.get("orderId"));
        
        try {
            List<Map<String, Object>> items = (List<Map<String, Object>>) event.get("items");
            if (items == null) return;

            for (Map<String, Object> item : items) {
                Long productId = Long.valueOf(String.valueOf(item.get("productId")));
                Integer quantityOrdered = (Integer) item.get("quantity");

                inventoryRepository.findByProductId(productId).ifPresent(inventory -> {
                    int newAvailable = inventory.getAvailableQuantity() - quantityOrdered;
                    // Ensure we don't go below zero
                    inventory.setAvailableQuantity(Math.max(0, newAvailable));
                    
                    // Simple logic: total stock also decreases on sale
                    if (inventory.getTotalQuantity() != null) {
                        inventory.setTotalQuantity(Math.max(0, inventory.getTotalQuantity() - quantityOrdered));
                    }
                    
                    inventoryRepository.save(inventory);
                    System.out.println("Stock updated for Product ID " + productId + ". New available: " + inventory.getAvailableQuantity());
                });
            }
        } catch (Exception e) {
            System.err.println("Failed to update inventory for order " + event.get("orderId") + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
