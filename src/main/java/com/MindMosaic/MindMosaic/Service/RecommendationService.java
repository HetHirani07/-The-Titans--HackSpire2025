package com.MindMosaic.MindMosaic.Service;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationService {

    public List<String> generateRecommendations(String sentiment) {
        List<String> recommendations = new ArrayList<>();
        if ("negative".equalsIgnoreCase(sentiment)) {
            recommendations.add("Practice mindfulness");
            recommendations.add("Reach out to friends");
            recommendations.add("Consult a therapist");
        } else {
            recommendations.add("Keep up the good work!");
        }
        return recommendations;
    }
}
