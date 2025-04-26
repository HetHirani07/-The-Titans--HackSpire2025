package com.MindMosaic.MindMosaic.Controller;

import com.MindMosaic.MindMosaic.DTO.ChatRequest;
import com.MindMosaic.MindMosaic.DTO.ChatResponse;
import com.MindMosaic.MindMosaic.Model.ChatMessage;
import com.MindMosaic.MindMosaic.Repository.ChatMessageRepository;
import com.MindMosaic.MindMosaic.Service.GeminiService;
import com.MindMosaic.MindMosaic.Service.RecommendationService;
import com.MindMosaic.MindMosaic.Service.SentimentAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final SentimentAnalysisService sentimentAnalysisService;
    private final GeminiService geminiService;
    private final RecommendationService recommendationService;
    private final ChatMessageRepository chatMessageRepository;

    @PostMapping("/send")
    public ChatResponse sendChat(@RequestBody ChatRequest chatRequest) throws Exception {
        String userInput = chatRequest.getMessage();
        String sentiment = sentimentAnalysisService.analyzeSentiment(userInput);
        String botReply = geminiService.generateReply(userInput);
        List<String> recommendations = recommendationService.generateRecommendations(sentiment);

        // Save chat
        ChatMessage chatMessage = ChatMessage.builder()
                .userId("userId") // hardcoded for now
                .userMessage(userInput)
                .botReply(botReply)
                .sentiment(sentiment)
                .mentalHealthConcern("negative".equalsIgnoreCase(sentiment))
                .recommendations(recommendations)
                .timestamp(LocalDateTime.now())
                .build();
        chatMessageRepository.save(chatMessage);

        return ChatResponse.builder()
                .botReply(botReply)
                .sentiment(sentiment)
                .recommendations(recommendations)
                .build();
    }
}

