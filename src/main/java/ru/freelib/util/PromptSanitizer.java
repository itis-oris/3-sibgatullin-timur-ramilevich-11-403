package ru.freelib.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public final class PromptSanitizer {
    public static String sanitizeInput(String input, int maxLength) {
        if (input == null || input.isBlank()) return "";
        String cleaned = input.trim().replaceAll("\\s+", " ").replaceAll("<[^>]+>", "");
        return cleaned.length() > maxLength ? cleaned.substring(0, maxLength) : cleaned;
    }

    public static String sanitizeOutput(String output, int maxLength) {
        if (output == null) return "";
        String cleaned = output.trim().replaceAll("\\s+", " ");
        return cleaned.length() > maxLength ? cleaned.substring(0, maxLength) + "..." : cleaned;
    }
}