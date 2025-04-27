package com.MindMosaic.MindMosaic.Service;

import com.MindMosaic.MindMosaic.DTO.ChatResponse;
import com.MindMosaic.MindMosaic.Model.ChatMessage;
import com.MindMosaic.MindMosaic.Model.User;
import com.MindMosaic.MindMosaic.Repository.ChatMessageRepository;
import com.MindMosaic.MindMosaic.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final SentimentAnalysisService sentimentService;
    private final GeminiService geminiService;
    private final RecommendationService recommendationService;
    private final ChatMessageRepository chatRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatResponse processChat(String message, String userEmail) throws Exception {
        // 1. Analyze sentiment
        String sentiment = sentimentService.analyzeSentiment(message);
        float sentimentScore = sentimentService.analyzeSentimentScore(message);

        // 2. Get AI response
        String botReply = geminiService.generateReply(message);

        // 3. Generate recommendations
        List<String> recommendations = recommendationService.getRecommendations(sentiment);

        // 4. Save chat message
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChatMessage chatMessage = ChatMessage.builder()
                .userId(user.getEmail())
                .userMessage(message)
                .botReply(botReply)
                .sentiment(sentiment)
                .sentimentScore(sentimentScore)
                .recommendations(recommendations)
                .timestamp(LocalDateTime.now())
                .build();

        chatRepository.save(chatMessage);

        // 5. Return response
        return ChatResponse.builder()
                .botReply(botReply)
                .sentiment(sentiment)
                .sentimentScore(sentimentScore)
                .recommendations(recommendations)
                .build();
    }

    public List<ChatMessage> getChatHistory(String userEmail) {
        return chatRepository.findByUserIdOrderByTimestampAsc(userEmail);
    }
}