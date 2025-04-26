package com.MindMosaic.MindMosaic.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class GeminiService {
    @Value("${gemini.api-key}")
    private String apiKey;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateReply(String userInput) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, String> part = new HashMap<>();

        part.put("text", "You are a mental health chatbot. " +
                "Respond with empathy and provide supportive advice to: " + userInput);
        content.put("parts", List.of(part));
        requestBody.put("contents", List.of(content));

        RequestBody body = RequestBody.create(
                objectMapper.writeValueAsString(requestBody),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent")
                .header("x-goog-api-key", apiKey)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Gemini API error: " + response.code());
            }

            String responseBody = response.body().string();
            Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) result.get("candidates");
            Map<String, Object> content1 = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content1.get("parts");

            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            log.error("Error generating reply: ", e);
            throw new RuntimeException("Failed to generate reply");
        }
    }
}

