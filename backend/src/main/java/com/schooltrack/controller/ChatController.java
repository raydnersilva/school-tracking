package com.schooltrack.controller;

import com.schooltrack.dto.ChatMessageRequest;
import com.schooltrack.dto.ChatMessageResponse;
import com.schooltrack.model.ChatMessage;
import com.schooltrack.repository.ChatMessageRepository;
import com.schooltrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/{roomId}")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(@PathVariable String roomId) {
        return ResponseEntity.ok(
                chatMessageRepository.findByRoomIdOrderBySentAtAsc(roomId).stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @GetMapping("/{roomId}/unread")
    public ResponseEntity<Long> getUnreadCount(@PathVariable String roomId) {
        return ResponseEntity.ok(chatMessageRepository.countByRoomIdAndReadByRecipientFalse(roomId));
    }

    @PutMapping("/{roomId}/read")
    public ResponseEntity<Void> markAllRead(@PathVariable String roomId) {
        var messages = chatMessageRepository.findByRoomIdAndReadByRecipientFalse(roomId);
        messages.forEach(m -> m.setReadByRecipient(true));
        chatMessageRepository.saveAll(messages);
        return ResponseEntity.ok().build();
    }

    @MessageMapping("/chat/send")
    public void sendMessage(ChatMessageRequest request, Authentication authentication) {
        var user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        ChatMessage message = ChatMessage.builder()
                .roomId(request.roomId())
                .sender(user)
                .content(request.content())
                .build();

        chatMessageRepository.save(message);

        ChatMessageResponse response = toResponse(message);
        messagingTemplate.convertAndSend("/topic/chat/" + request.roomId(), response);
    }

    private ChatMessageResponse toResponse(ChatMessage msg) {
        return new ChatMessageResponse(
                msg.getId(),
                msg.getRoomId(),
                msg.getSender().getId(),
                msg.getSender().getName(),
                msg.getContent(),
                msg.getSentAt(),
                msg.isReadByRecipient()
        );
    }
}
