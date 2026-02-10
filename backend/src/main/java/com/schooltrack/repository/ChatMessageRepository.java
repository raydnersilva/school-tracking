package com.schooltrack.repository;

import com.schooltrack.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByRoomIdOrderBySentAtAsc(String roomId);
    List<ChatMessage> findByRoomIdAndReadByRecipientFalse(String roomId);
    long countByRoomIdAndReadByRecipientFalse(String roomId);
}
