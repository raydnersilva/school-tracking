package com.schooltrack.dto;

public record ChatMessageRequest(
        String roomId,
        String content
) {}
