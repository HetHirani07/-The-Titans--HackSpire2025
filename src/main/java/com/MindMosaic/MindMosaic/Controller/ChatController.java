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
    float sentimentScore = (float) sentimentAnalysisService.analyzeSentimentScore(userInput); // Cast to float

    String botReply = geminiService.generateReply(userInput);
    List<String> recommendations = recommendationService.generateRecommendations(sentiment);

    ChatMessage chatMessage = ChatMessage.builder()
            .userId("userId")
            .userMessage(userInput)
            .botReply(botReply)
            .sentiment(sentiment)
            .sentimentScore(sentimentScore)
            .mentalHealthConcern("negative".equalsIgnoreCase(sentiment))
            .recommendations(recommendations)
            .timestamp(LocalDateTime.now())
            .build();
    chatMessageRepository.save(chatMessage);

    return ChatResponse.builder()
            .botReply(botReply)
            .sentiment(sentiment)
            .sentimentScore(sentimentScore)
            .recommendations(recommendations)
            .build();
}

    @GetMapping("/history/{userId}")
public List<ChatMessage> getChatHistory(@PathVariable String userId) {
    return chatMessageRepository.findByUserIdOrderByTimestampAsc(userId);
}

  @GetMapping("/sentiment-summary/{userId}")
public Map<String, Object> getSentimentSummary(@PathVariable String userId) {
    List<ChatMessage> chats = chatMessageRepository.findByUserId(userId);
    double avgScore = chats.stream().mapToDouble(ChatMessage::getSentimentScore).average().orElse(0.0);
    long positive = chats.stream().filter(c -> c.getSentiment().equals("positive")).count();
    long negative = chats.stream().filter(c -> c.getSentiment().equals("negative")).count();
    return Map.of(
        "averageScore", avgScore,
        "positiveCount", positive,
        "negativeCount", negative
    );
} 

    @GetMapping("/recommendations/{userId}")
public List<String> getUserRecommendations(@PathVariable String userId) {
    List<ChatMessage> chats = chatMessageRepository.findByUserId(userId);
    return chats.stream()
        .flatMap(c -> c.getRecommendations().stream())
        .distinct()
        .toList();
}
}

