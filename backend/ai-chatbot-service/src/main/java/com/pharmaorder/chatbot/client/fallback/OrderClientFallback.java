package com.pharmaorder.chatbot.client.fallback;

import com.pharmaorder.chatbot.client.OrderServiceClient;
import com.pharmaorder.chatbot.dto.context.OrderSummaryDto;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class OrderClientFallback implements OrderServiceClient {
    @Override
    public List<OrderSummaryDto> getRecentOrdersForUser(UUID userId, int limit) {
        return Collections.emptyList();
    }
}
