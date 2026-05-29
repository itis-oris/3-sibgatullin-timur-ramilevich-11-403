package ru.freelib.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class IdempotencyService {

    private static final String SESSION_KEY = "idempotency_tokens";

    @SuppressWarnings("unchecked")
    public String generateToken(HttpSession session) {
        String token = UUID.randomUUID().toString();
        Set<String> tokens = (Set<String>) session.getAttribute(SESSION_KEY);
        if (tokens == null) {
            tokens = new HashSet<>();
            session.setAttribute("idempotency_tokens", tokens);
        }
        tokens.add(token);
        return token;
    }

    @SuppressWarnings("unchecked")
    public boolean validateAndConsume(HttpSession session, String submittedToken) {
        if (submittedToken == null || submittedToken.isBlank()) {
            return false;
        }
        Set<String> tokens = (Set<String>) session.getAttribute(SESSION_KEY);
        return tokens != null && tokens.remove(submittedToken);
    }
}