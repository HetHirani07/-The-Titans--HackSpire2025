package com.MindMosaic.MindMosaic.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String userMessage;
    private String botReply;
    private String sentiment;
    private boolean mentalHealthConcern;
    private float sentimentScore;

    @ElementCollection
    private List<String> recommendations;

    private LocalDateTime timestamp;
}

