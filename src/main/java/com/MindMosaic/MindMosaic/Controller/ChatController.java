package com.MindMosaic.MindMosaic.Controller;

import com.MindMosaic.MindMosaic.DTO.ChatResponse;
import com.MindMosaic.MindMosaic.Model.ChatMessage;
import com.MindMosaic.MindMosaic.Service.ChatService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/send")
    public ResponseEntity<ChatResponse> sendMessage(
            @RequestBody MessageRequest messageRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            ChatResponse response = chatService.processChat(
                    messageRequest.getMessage(),
                    userDetails.getUsername()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing chat: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<String>> getRecommendations(
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<ChatMessage> userChats = chatService.getChatHistory(userDetails.getUsername());
            List<String> recommendations = userChats.stream()
                    .flatMap(chat -> chat.getRecommendations().stream())
                    .distinct()
                    .collect(Collectors.toList());
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            log.error("Error fetching recommendations: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

@Data
class MessageRequest {
    private String message;
}