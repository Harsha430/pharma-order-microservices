package com.pharmaorder.chatbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmaorder.chatbot.client.OrderServiceClient;
import com.pharmaorder.chatbot.client.PrescriptionServiceClient;
import com.pharmaorder.chatbot.client.ProductServiceClient;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ContextEnricherService {

    private final ProductServiceClient productClient;
    private final OrderServiceClient orderClient;
    private final PrescriptionServiceClient prescriptionClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ContextEnricherService(ProductServiceClient productClient, OrderServiceClient orderClient, PrescriptionServiceClient prescriptionClient) {
        this.productClient = productClient;
        this.orderClient = orderClient;
        this.prescriptionClient = prescriptionClient;
    }

    public String buildContextVariables(String userMessage, UUID userId) {
        StringBuilder context = new StringBuilder();

        // Very basic intent detection based on the spec
        String lowerMsg = userMessage.toLowerCase();

        try {
            if (lowerMsg.contains("order") || lowerMsg.contains("delivery") || lowerMsg.contains("track") || lowerMsg.contains("shipped")) {
                var orders = orderClient.getRecentOrdersForUser(userId, 3);
                if (!orders.isEmpty()) {
                    context.append("Orders Context: ").append(objectMapper.writeValueAsString(orders)).append("\n");
                }
            }

            if (lowerMsg.contains("prescription") || lowerMsg.contains("rx") || lowerMsg.contains("approved")) {
                var prescriptions = prescriptionClient.getPrescriptionsForUser(userId);
                if (!prescriptions.isEmpty()) {
                    context.append("Prescriptions Context: ").append(objectMapper.writeValueAsString(prescriptions)).append("\n");
                }
            }

            if (lowerMsg.contains("find") || lowerMsg.contains("search") || lowerMsg.contains("medicine") || 
                lowerMsg.contains("fever") || lowerMsg.contains("pain") || lowerMsg.contains("headache") || 
                lowerMsg.contains("cold") || lowerMsg.contains("cough") || lowerMsg.contains("indigestion") || 
                lowerMsg.contains("tablet") || lowerMsg.contains("capsule")) {
                // simple search extract
                var products = productClient.searchProducts(userMessage.replaceAll("[^a-zA-Z0-9 ]", ""), 5);
                if (!products.isEmpty()) {
                    context.append("Products Context: ").append(objectMapper.writeValueAsString(products)).append("\n");
                }
            }
        } catch (Exception e) {
            // Ignore downstream failures and return whatever context we managed to get
            context.append("[Context fetching partially failed]");
        }

        return context.toString();
    }
}
