package com.pharmaorder.chatbot.client;

import com.pharmaorder.chatbot.dto.context.ProductSummaryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "product-service", fallback = com.pharmaorder.chatbot.client.fallback.ProductClientFallback.class)
public interface ProductServiceClient {
    @GetMapping("/api/v1/products/search")
    List<ProductSummaryDto> searchProducts(@RequestParam("q") String query, @RequestParam(value = "size", defaultValue = "5") int size);
}
