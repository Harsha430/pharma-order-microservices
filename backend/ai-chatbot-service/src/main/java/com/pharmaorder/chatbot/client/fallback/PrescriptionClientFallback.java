package com.pharmaorder.chatbot.client.fallback;

import com.pharmaorder.chatbot.client.PrescriptionServiceClient;
import com.pharmaorder.chatbot.dto.context.PrescriptionSummaryDto;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class PrescriptionClientFallback implements PrescriptionServiceClient {
    @Override
    public List<PrescriptionSummaryDto> getPrescriptionsForUser(UUID userId) {
        return Collections.emptyList();
    }
}
