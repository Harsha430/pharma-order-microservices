package com.pharmaorder.chatbot.service;

import com.pharmaorder.chatbot.entity.ChatRateLimit;
import com.pharmaorder.chatbot.repository.ChatRateLimitRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RateLimitService {

    private final ChatRateLimitRepository repository;

    @Value("${chatbot.rate-limit.messages-per-hour:50}")
    private int messagesPerHour;

    public RateLimitService(ChatRateLimitRepository repository) {
        this.repository = repository;
    }

    public void checkAndIncrementQuota(UUID userId) {
        ChatRateLimit rateLimit = repository.findById(userId).orElse(new ChatRateLimit(userId, 0, LocalDateTime.now()));

        if (rateLimit.getWindowStart().plusHours(1).isBefore(LocalDateTime.now())) {
            rateLimit.setWindowStart(LocalDateTime.now());
            rateLimit.setMessageCount(0);
        }

        if (rateLimit.getMessageCount() >= messagesPerHour) {
            throw new RuntimeException("Chat limit reached. Try again later.");
        }

        rateLimit.setMessageCount(rateLimit.getMessageCount() + 1);
        repository.save(rateLimit);
    }
}
