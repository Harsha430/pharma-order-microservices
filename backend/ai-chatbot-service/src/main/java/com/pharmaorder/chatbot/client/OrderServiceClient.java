package com.pharmaorder.chatbot.client;

import com.pharmaorder.chatbot.dto.context.OrderSummaryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order-service", fallback = com.pharmaorder.chatbot.client.fallback.OrderClientFallback.class)
public interface OrderServiceClient {
    @GetMapping("/api/v1/orders/user/{userId}")
    List<OrderSummaryDto> getRecentOrdersForUser(@PathVariable("userId") UUID userId, @RequestParam(value = "limit", defaultValue = "3") int limit);
}
