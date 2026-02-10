package com.schooltrack.dto;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long id,
        String roomId,
        Long senderId,
        String senderName,
        String content,
        LocalDateTime sentAt,
        boolean readByRecipient
) {}
