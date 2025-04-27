package com.MindMosaic.MindMosaic.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class SentimentAnalysisService {
    @Value("${huggingface.api-key}")
    private String apiKey;

    @Value("${huggingface.model-id}")
    private String modelId;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String analyzeSentiment(String message) throws Exception {
        RequestBody body = RequestBody.create(
                objectMapper.writeValueAsString(Map.of("inputs", message)),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url("https://api-inference.huggingface.co/models/" + modelId)
                .header("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Hugging Face API error: " + response.code());
            }

            String responseBody = response.body().string();
            List<List<Map<String, Object>>> results = objectMapper.readValue(responseBody,
                    new TypeReference<List<List<Map<String, Object>>>>() {});

            // Extract sentiment from the first result
            Map<String, Object> positiveResult = results.get(0).stream()
                    .filter(entry -> "POSITIVE".equalsIgnoreCase((String) entry.get("label")))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("POSITIVE label not found"));

            Map<String, Object> negativeResult = results.get(0).stream()
                    .filter(entry -> "NEGATIVE".equalsIgnoreCase((String) entry.get("label")))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("NEGATIVE label not found"));

            double positiveScore = (double) positiveResult.get("score");
            double negativeScore = (double) negativeResult.get("score");

            return positiveScore > negativeScore ? "positive" : "negative";
        } catch (Exception e) {
            log.error("Error analyzing sentiment: ", e);
            throw new RuntimeException("Failed to analyze sentiment");
        }
    }

    public float analyzeSentimentScore(String message) throws Exception {
        RequestBody body = RequestBody.create(
                objectMapper.writeValueAsString(Map.of("inputs", message)),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url("https://api-inference.huggingface.co/models/" + modelId)
                .header("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Hugging Face API error: " + response.code());
            }

            String responseBody = response.body().string();
            List<List<Map<String, Object>>> results = objectMapper.readValue(responseBody,
                    new TypeReference<List<List<Map<String, Object>>>>() {});

            // Extract scores
            Map<String, Object> positiveResult = results.get(0).stream()
                    .filter(entry -> "POSITIVE".equalsIgnoreCase((String) entry.get("label")))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("POSITIVE label not found"));

            Map<String, Object> negativeResult = results.get(0).stream()
                    .filter(entry -> "NEGATIVE".equalsIgnoreCase((String) entry.get("label")))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("NEGATIVE label not found"));

            double positiveScore = (double) positiveResult.get("score");
            double negativeScore = (double) negativeResult.get("score");

            return (float) (positiveScore - negativeScore);
        } catch (Exception e) {
            log.error("Error analyzing sentiment score: ", e);
            throw new RuntimeException("Failed to analyze sentiment score");
        }
    }
}

