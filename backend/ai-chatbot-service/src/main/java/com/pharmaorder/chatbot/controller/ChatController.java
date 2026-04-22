package com.pharmaorder.chatbot.controller;

import com.pharmaorder.chatbot.dto.request.ChatRequest;
import com.pharmaorder.chatbot.entity.ChatMessage;
import com.pharmaorder.chatbot.entity.ChatSession;
import com.pharmaorder.chatbot.service.ChatService;
import com.pharmaorder.chatbot.service.ConversationMemoryService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;
    private final ConversationMemoryService memoryService;

    public ChatController(ChatService chatService, ConversationMemoryService memoryService) {
        this.chatService = chatService;
        this.memoryService = memoryService;
    }

    @PostMapping(value = "/message", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> sendMessage(
            @RequestBody ChatRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userIdStr,
            @RequestHeader(value = "X-User-Email", required = false) String email,
            @RequestHeader(value = "X-User-First-Name", required = false) String firstName,
            @RequestHeader(value = "X-User-Last-Name", required = false) String lastName,
            @RequestHeader(value = "X-User-Roles", required = false) String roles) {
        
        UUID userId = userIdStr != null ? UUID.fromString(userIdStr) : UUID.fromString("00000000-0000-0000-0000-000000000000"); // fallback for testing if gateway bypasses
        return chatService.streamChat(request, userId, email, firstName, lastName, roles);
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<ChatSession>> getSessions(@RequestHeader(value = "X-User-Id") String userId) {
        return ResponseEntity.ok(chatService.getUserSessions(UUID.fromString(userId)));
    }

    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<List<ChatMessage>> getSessionMessages(
            @PathVariable UUID sessionId,
            @RequestHeader(value = "X-User-Id") String userIdStr) {
        
        // Ownership check happens in a real app, assuming simple access here for hackathon
        return ResponseEntity.ok(memoryService.getRecentMessages(sessionId));
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> deleteSession(
            @PathVariable UUID sessionId,
            @RequestHeader(value = "X-User-Id") String userId) {
        chatService.deleteSession(sessionId, UUID.fromString(userId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AI Chatbot Service is UP");
    }
}
