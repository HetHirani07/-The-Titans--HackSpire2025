package com.MindMosaic.MindMosaic.Repository;


import com.MindMosaic.MindMosaic.Model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}

