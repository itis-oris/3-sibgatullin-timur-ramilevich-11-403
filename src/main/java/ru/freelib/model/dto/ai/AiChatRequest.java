package ru.freelib.model.dto.ai;

import java.util.List;

public record AiChatRequest(
        String model,
        List<Message> messages,
        double temperature,
        int max_tokens
) {
    public record Message(String role, String content) {}
}