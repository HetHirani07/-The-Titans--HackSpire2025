package com.MindMosaic.MindMosaic.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "chat_messages")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @Column(columnDefinition = "TEXT")
    private String userMessage;

    @Column(columnDefinition = "TEXT")
    private String botReply;

    private String sentiment;
    private boolean mentalHealthConcern;
    private float sentimentScore;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "chat_message_recommendations",
            joinColumns = @JoinColumn(name = "chat_message_id")
    )
    @Column(name = "recommendation", columnDefinition = "TEXT")
    private List<String> recommendations = new ArrayList<>();

    private LocalDateTime timestamp;
}