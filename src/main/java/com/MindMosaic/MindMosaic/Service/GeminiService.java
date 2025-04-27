package com.MindMosaic.MindMosaic.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.MindMosaic.MindMosaic.Exception.AIServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class GeminiService {
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    @Value("${gemini.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeminiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public String generateReply(String userInput) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", apiKey);

            ObjectNode requestBody = objectMapper.createObjectNode();
            ObjectNode contents = objectMapper.createObjectNode();
            contents.put("role", "user");
            contents.put("text", userInput);
            requestBody.putArray("contents").add(contents);

            ObjectNode generationConfig = objectMapper.createObjectNode();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("maxOutputTokens", 500);
            requestBody.set("generationConfig", generationConfig);

            HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);
            String response = restTemplate.postForObject(API_URL, request, String.class);

            JsonNode responseJson = objectMapper.readTree(response);
            return extractTextFromResponse(responseJson);

        } catch (Exception e) {
            log.error("Failed to generate response", e);
            throw new AIServiceException("Failed to generate response", e);
        }
    }

    private String extractTextFromResponse(JsonNode response) {
        try {
            return response.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        } catch (Exception e) {
            throw new AIServiceException("Failed to parse response", e);
        }
    }
}