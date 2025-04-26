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
            List<Map<String, Double>> results = objectMapper.readValue(responseBody,
                    new TypeReference<List<Map<String, Double>>>() {});

            // Extract sentiment from response
            Map<String, Double> scores = results.get(0);
            return scores.get("POSITIVE") > scores.get("NEGATIVE") ? "positive" : "negative";
        } catch (Exception e) {
            log.error("Error analyzing sentiment: ", e);
            throw new RuntimeException("Failed to analyze sentiment");
        }
    }
}

