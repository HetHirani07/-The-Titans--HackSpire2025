package com.MindMosaic.MindMosaic.Repository;

import java.util.List;
import com.MindMosaic.MindMosaic.Model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByUserId(String userId);
    List<ChatMessage> findByMentalHealthConcern(boolean mentalHealthConcern);
    List<ChatMessage> findByUserIdOrderByTimestampAsc(String userId);
}

