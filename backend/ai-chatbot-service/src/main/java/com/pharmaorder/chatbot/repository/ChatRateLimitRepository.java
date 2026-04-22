package com.pharmaorder.chatbot.repository;

import com.pharmaorder.chatbot.entity.ChatRateLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatRateLimitRepository extends JpaRepository<ChatRateLimit, UUID> {
}
