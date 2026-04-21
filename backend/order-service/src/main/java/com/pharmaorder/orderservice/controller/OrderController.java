package com.pharmaorder.orderservice.controller;

import com.pharmaorder.orderservice.model.entity.Order;
import com.pharmaorder.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate;

    public OrderController(OrderRepository orderRepository, org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate) {
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable java.util.UUID userId) {
        return ResponseEntity.ok(orderRepository.findByUserId(userId));
    }

    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(@RequestBody Order order) {
        order.setStatus("PENDING");
        order.setPlacedAt(java.time.LocalDateTime.now());
        
        // Link children to parent for JPA
        if (order.getItems() != null) {
            order.getItems().forEach(item -> item.setOrder(order));
        }

        Order savedOrder = orderRepository.save(order);

        // Publish event for notification-service
        try {
            java.util.Map<String, Object> event = new java.util.HashMap<>();
            event.put("orderId", savedOrder.getId() != null ? savedOrder.getId().toString() : "UNKNOWN");
            event.put("userId", savedOrder.getUserId() != null ? savedOrder.getUserId().toString() : "UNKNOWN");
            event.put("userEmail", savedOrder.getUserEmail() != null ? savedOrder.getUserEmail() : "UNKNOWN");
            event.put("totalAmount", savedOrder.getTotalAmount() != null ? savedOrder.getTotalAmount() : java.math.BigDecimal.ZERO);
            event.put("status", savedOrder.getStatus());
            
            // Include simplified items for the email template
            if (savedOrder.getItems() != null) {
                java.util.List<java.util.Map<String, Object>> items = savedOrder.getItems().stream().map(item -> {
                    java.util.Map<String, Object> i = new java.util.HashMap<>();
                    i.put("productId", item.getProductId());
                    i.put("quantity", item.getQuantity() != null ? item.getQuantity() : 0);
                    i.put("unitPrice", item.getUnitPrice() != null ? item.getUnitPrice() : java.math.BigDecimal.ZERO);
                    return i;
                }).collect(java.util.stream.Collectors.toList());
                event.put("items", items);
            }
            
            rabbitTemplate.convertAndSend("order.exchange", "order.created", event);
            System.out.println("Notification event published for Order: " + savedOrder.getId());
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }

        return ResponseEntity.ok(savedOrder);
    }
}
