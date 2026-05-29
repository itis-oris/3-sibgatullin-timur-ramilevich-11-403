package ru.freelib.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.freelib.model.dto.BookDto;
import ru.freelib.service.BookService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookApiController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<Page<BookDto>> list(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String authorName,
            @RequestParam(required = false) List<Long> genreIds,
            @RequestParam(required = false) Long minViews,
            @RequestParam(required = false) Long maxViews,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        String[] parts = sort.split(",");
        Sort.Direction dir = parts.length > 1 && "asc".equalsIgnoreCase(parts[1])
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, parts[0]));

        Page<BookDto> pageResult = bookService.search(title, authorName, genreIds, minViews, maxViews, pageable);
        return ResponseEntity.ok(pageResult);
    }
}