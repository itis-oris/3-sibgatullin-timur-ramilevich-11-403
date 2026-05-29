package ru.freelib.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.freelib.security.CustomUserDetails;
import ru.freelib.service.FavoriteService;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteApiController {

    private final FavoriteService favoriteService;

    @PostMapping("/{bookId}")
    public ResponseEntity<Map<String, String>> add(@PathVariable Long bookId,
                                                   @AuthenticationPrincipal CustomUserDetails user) {
        favoriteService.add(user.getId(), bookId);
        return ResponseEntity.ok(Map.of("status", "added"));
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Map<String, String>> remove(@PathVariable Long bookId,
                                                      @AuthenticationPrincipal CustomUserDetails user) {
        favoriteService.remove(user.getId(), bookId);
        return ResponseEntity.ok(Map.of("status", "removed"));
    }
}