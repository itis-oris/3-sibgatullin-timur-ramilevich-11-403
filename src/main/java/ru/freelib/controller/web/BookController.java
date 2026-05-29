package ru.freelib.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.freelib.model.entity.Book;
import ru.freelib.security.CustomUserDetails;
import ru.freelib.service.*;
import ru.freelib.util.FileStorageUtil;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final GenreService genreService;
    private final BookService bookService;
    private final CommentService commentService;
    private final FavoriteService favoriteService;
    private final FileStorageUtil fileStorage;
    private final RecommendationService recommendationService;

    @GetMapping("/book")
    public String viewBook(@RequestParam Long id,
                           Model model,
                           @AuthenticationPrincipal CustomUserDetails user) {
        bookService.incrementViews(id);
        model.addAttribute("book", bookService.getById(id));
        model.addAttribute("comments", commentService.getByBookId(id));
        if (user != null) {
            model.addAttribute("isFavorite", favoriteService.exists(user.getId(), id));
        }
        try {
            List<Book> similarBooks = recommendationService.findSimilarBooks(id, 5);
            model.addAttribute("similarBooks", similarBooks);
        } catch (Exception e) {
            model.addAttribute("similarBooks", Collections.emptyList());
        }
        return "book";
    }

    @GetMapping("/books")
    public String booksByGenre(@RequestParam Long genreId, Model model) {
        model.addAttribute("genre", genreService.getById(genreId));
        model.addAttribute("books", bookService.getByGenreId(genreId));
        return "books";
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadBook(@RequestParam Long id) {
        var book = bookService.getById(id);
        InputStream stream = fileStorage.getInputStream(book.getFilePath());

        String encodedName = URLEncoder.encode(
                book.getTitle() + book.getFilePath().substring(book.getFilePath().lastIndexOf('.')),
                StandardCharsets.UTF_8
        ).replace("+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedName + "\"")
                .body(new InputStreamResource(stream));
    }
}