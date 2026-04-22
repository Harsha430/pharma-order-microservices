package com.pharmaorder.chatbot.client.fallback;

import com.pharmaorder.chatbot.client.ProductServiceClient;
import com.pharmaorder.chatbot.dto.context.ProductSummaryDto;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ProductClientFallback implements ProductServiceClient {
    @Override
    public List<ProductSummaryDto> searchProducts(String query, int size) {
        return Collections.emptyList();
    }
}
