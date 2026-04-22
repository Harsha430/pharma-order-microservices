package com.pharmaorder.chatbot.service;

import com.pharmaorder.chatbot.dto.request.ChatRequest;
import com.pharmaorder.chatbot.entity.ChatMessage;
import com.pharmaorder.chatbot.entity.ChatSession;
import com.pharmaorder.chatbot.repository.ChatSessionRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    private final ChatClient chatClient;
    private final ConversationMemoryService memoryService;
    private final ContextEnricherService enricherService;
    private final PromptBuilderService promptBuilderService;
    private final RateLimitService rateLimitService;
    private final ChatSessionRepository sessionRepository;

    public ChatService(ChatClient.Builder chatClientBuilder,
                       ConversationMemoryService memoryService,
                       ContextEnricherService enricherService,
                       PromptBuilderService promptBuilderService,
                       RateLimitService rateLimitService,
                       ChatSessionRepository sessionRepository) {
        this.chatClient = chatClientBuilder.build();
        this.memoryService = memoryService;
        this.enricherService = enricherService;
        this.promptBuilderService = promptBuilderService;
        this.rateLimitService = rateLimitService;
        this.sessionRepository = sessionRepository;
    }

    public Flux<ServerSentEvent<String>> streamChat(ChatRequest request, UUID userId, String email, String firstName, String lastName, String roles) {
        // 1. Rate Limiting
        rateLimitService.checkAndIncrementQuota(userId);

        // 2. Input Sanitization
        String sanitizedMessage = sanitizeInput(request.getMessage());

        // 3. Conversation Memory
        ChatSession session = memoryService.getOrCreateSession(request.getSessionId(), userId, sanitizedMessage);
        
        // 4. Feign Context Enrichment
        String dynamicContext = enricherService.buildContextVariables(sanitizedMessage, userId);

        // 5. System Prompt Construction
        String systemPromptText = promptBuilderService.buildSystemPrompt(firstName, lastName, email, roles, dynamicContext);

        // 6. Assemble Message History for Spring AI
        List<Message> messageHistory = new ArrayList<>();
        messageHistory.add(new SystemMessage(systemPromptText));
        
        List<ChatMessage> previousMessages = memoryService.getRecentMessages(session.getId());
        for (ChatMessage msg : previousMessages) {
            if ("USER".equalsIgnoreCase(msg.getRole())) {
                messageHistory.add(new UserMessage(msg.getContent()));
            } else if ("ASSISTANT".equalsIgnoreCase(msg.getRole())) {
                messageHistory.add(new AssistantMessage(msg.getContent()));
            }
        }
        
        // Save current user message to db
        memoryService.saveMessage(session.getId(), "USER", sanitizedMessage);
        messageHistory.add(new UserMessage(sanitizedMessage));

        // 7. Call Spring AI ChatClient with Streaming
        Flux<String> tokenStream = chatClient.prompt()
                .messages(messageHistory)
                .stream()
                .content();

        // 8. Stream the events and accumulate full response on completion
        StringBuilder assistantResponse = new StringBuilder();

        return tokenStream.map(token -> {
            assistantResponse.append(token);
            return ServerSentEvent.<String>builder()
                    .event("token")
                    .data(token)
                    .build();
        }).concatWith(Flux.defer(() -> {
            // On completion, save assistant message
            memoryService.saveMessage(session.getId(), "ASSISTANT", assistantResponse.toString());
            return Flux.just(ServerSentEvent.<String>builder()
                    .event("done")
                    .data(session.getId().toString())
                    .build());
        })).onErrorResume(e -> {
            if (e instanceof org.springframework.web.reactive.function.client.WebClientResponseException) {
                var wcre = (org.springframework.web.reactive.function.client.WebClientResponseException) e;
                logger.error("Groq API Error: Status={}, Body={}", wcre.getStatusCode(), wcre.getResponseBodyAsString());
            } else {
                logger.error("Error processing chat request for session {}: {}", session.getId(), e.getMessage(), e);
            }
            return Flux.just(ServerSentEvent.<String>builder()
                .event("error")
                .data("An error occurred processing your request. Please try again.")
                .build());
        });
    }

    private String sanitizeInput(String input) {
        if (input == null) return "";
        String sanitized = input.replaceAll("<[^>]*>", ""); // basic HTML strip
        if (sanitized.length() > 2000) sanitized = sanitized.substring(0, 2000);
        return sanitized;
    }

    public void deleteSession(UUID sessionId, UUID userId) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        if (!session.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        sessionRepository.delete(session);
    }

    public List<ChatSession> getUserSessions(UUID userId) {
        return sessionRepository.findByUserIdOrderByLastActiveDesc(userId);
    }
}
