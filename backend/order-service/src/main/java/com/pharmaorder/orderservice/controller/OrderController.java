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
    public ResponseEntity<?> checkout(@RequestBody Order order) {
        // Simple synchronous validation for demo purposes
        // In production, we would use Feign clients or a dedicated service layer
        org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
        String productServiceUrl = "http://product-service/api/v1/products/"; // Using service names if Eureka is functional
        // For local development without Eureka routing in RestTemplate, we'd use localhost ports
        String localProductUrl = "http://product-service:8082/api/v1/products/";
        String localPrescriptionUrl = "http://prescription-service:8084/api/v1/prescriptions/";

        boolean prescriptionRequired = false;
        
        try {
            if (order.getItems() != null) {
                for (com.pharmaorder.orderservice.model.entity.OrderItem item : order.getItems()) {
                    try {
                        java.util.Map<String, Object> product = restTemplate.getForObject(localProductUrl + item.getProductId(), java.util.Map.class);
                        if (product != null && Boolean.TRUE.equals(product.get("prescriptionRequired"))) {
                            prescriptionRequired = true;
                            break;
                        }
                    } catch (Exception e) {
                        System.err.println("Warning: Could not verify product " + item.getProductId() + ": " + e.getMessage());
                        // Proceeding for demo if service is unreachable
                    }
                }
            }

            if (prescriptionRequired && (order.getPrescriptionId() == null)) {
                return ResponseEntity.badRequest().body(java.util.Map.of("message", "A valid prescription is required for one or more items in your cart."));
            }

            if (prescriptionRequired && order.getPrescriptionId() != null) {
                Boolean isValid = restTemplate.getForObject(localPrescriptionUrl + order.getPrescriptionId() + "/verify?userId=" + order.getUserId(), Boolean.class);
                if (Boolean.FALSE.equals(isValid)) {
                    return ResponseEntity.badRequest().body(java.util.Map.of("message", "The provided prescription is invalid or does not belong to your account."));
                }
            }
        } catch (Exception e) {
            System.err.println("Validation service error: " + e.getMessage());
        }

        order.setStatus("PENDING");
        order.setPlacedAt(java.time.LocalDateTime.now());
        
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
            event.put("pointsRedeemed", savedOrder.getPointsRedeemed() != null ? savedOrder.getPointsRedeemed() : 0);
            event.put("status", savedOrder.getStatus());
            event.put("prescriptionId", savedOrder.getPrescriptionId());
            
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
