package com.MindMosaic.MindMosaic.Service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecommendationService {
    private final Map<String, List<String>> recommendationMap;

    public RecommendationService() {
        recommendationMap = new HashMap<>();
        recommendationMap.put("negative", Arrays.asList(
                "Try deep breathing exercises",
                "Consider talking to a therapist",
                "Practice mindfulness meditation",
                "Take a break and go for a walk"
        ));
        recommendationMap.put("positive", Arrays.asList(
                "Keep up the good work!",
                "Share your positivity with others",
                "Journal about your positive experiences",
                "Continue your healthy habits"
        ));
    }

    public List<String> getRecommendations(String sentiment) {
        List<String> recommendations = recommendationMap.get(sentiment.toLowerCase());
        if (recommendations == null) {
            return Collections.emptyList();
        }
        Collections.shuffle(recommendations);
        return recommendations.subList(0, Math.min(2, recommendations.size()));
    }
}
