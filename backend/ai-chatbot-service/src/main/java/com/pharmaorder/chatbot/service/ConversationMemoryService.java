package com.pharmaorder.chatbot.service;

import com.pharmaorder.chatbot.entity.ChatMessage;
import com.pharmaorder.chatbot.entity.ChatSession;
import com.pharmaorder.chatbot.repository.ChatMessageRepository;
import com.pharmaorder.chatbot.repository.ChatSessionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ConversationMemoryService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;

    @Value("${chatbot.memory.window-size:10}")
    private int windowSize;

    public ConversationMemoryService(ChatSessionRepository sessionRepository, ChatMessageRepository messageRepository) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
    }

    public ChatSession getOrCreateSession(UUID sessionId, UUID userId, String initialMessage) {
        if (sessionId != null) {
            return sessionRepository.findById(sessionId)
                    .map(session -> {
                        session.setLastActive(LocalDateTime.now());
                        return sessionRepository.save(session);
                    }).orElseGet(() -> createNewSession(userId, initialMessage));
        }
        return createNewSession(userId, initialMessage);
    }

    private ChatSession createNewSession(UUID userId, String initialMessage) {
        String title = initialMessage.length() > 30 ? initialMessage.substring(0, 30) + "..." : initialMessage;
        ChatSession session = new ChatSession(UUID.randomUUID(), userId, title, LocalDateTime.now(), LocalDateTime.now(), true);
        return sessionRepository.save(session);
    }

    public List<ChatMessage> getRecentMessages(UUID sessionId) {
        List<ChatMessage> allMessages = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        if (allMessages.size() > windowSize) {
            return allMessages.subList(allMessages.size() - windowSize, allMessages.size());
        }
        return allMessages;
    }

    public void saveMessage(UUID sessionId, String role, String content) {
        ChatMessage message = new ChatMessage(UUID.randomUUID(), sessionId, role, content, 0, LocalDateTime.now());
        messageRepository.save(message);
    }
}
