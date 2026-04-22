package com.pharmaorder.chatbot.client;

import com.pharmaorder.chatbot.dto.context.PrescriptionSummaryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "prescription-service", fallback = com.pharmaorder.chatbot.client.fallback.PrescriptionClientFallback.class)
public interface PrescriptionServiceClient {
    @GetMapping("/api/v1/prescriptions/user/{userId}")
    List<PrescriptionSummaryDto> getPrescriptionsForUser(@PathVariable("userId") UUID userId);
}
