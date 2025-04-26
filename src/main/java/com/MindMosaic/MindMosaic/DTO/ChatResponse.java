package com.MindMosaic.MindMosaic.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatResponse {
    private String botReply;
    private String sentiment;
    private List<String> recommendations;
    private float sentimentScore;
}

