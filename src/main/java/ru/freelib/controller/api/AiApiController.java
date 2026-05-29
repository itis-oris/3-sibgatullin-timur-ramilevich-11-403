package ru.freelib.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.freelib.service.AiDescriptionService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Tag(name = "AI-генерация", description = "Интеграция с LLM для генерации описаний книг")
public class AiApiController {

    private final AiDescriptionService aiDescriptionService;

    @Operation(summary = "Сгенерировать описание книги",
            description = "Использует локальную LLM (запущенную в llama.cpp) для создания описания")
    @PostMapping("/generate-description")
    public ResponseEntity<Map<String, String>> generateDescription(@RequestBody Map<String, Object> payload) {
        String title = (String) payload.get("title");
        String author = (String) payload.get("author");
        @SuppressWarnings("unchecked")
        List<String> genres = (List<String>) payload.get("genres");

        String description = aiDescriptionService.generateDescription(title, author, genres);
        return ResponseEntity.ok(Map.of("description", description));
    }

    @Operation(summary = "Улучшить существующее описание",
            description = "Редактирует черновик, сохраняя смысл и убирая воду")
    @PostMapping("/improve-description")
    public ResponseEntity<Map<String, String>> improveDescription(@RequestBody Map<String, Object> payload) {
        String existingDesc = (String) payload.get("existingDesc");
        String title = (String) payload.get("title");
        String author = (String) payload.get("author");
        @SuppressWarnings("unchecked")
        List<String> genres = (List<String>) payload.get("genres");

        String description = aiDescriptionService.improveDescription(existingDesc, title, author, genres);
        return ResponseEntity.ok(Map.of("description", description));
    }
}