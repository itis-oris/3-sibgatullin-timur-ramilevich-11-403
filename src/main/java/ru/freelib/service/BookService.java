package ru.freelib.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.freelib.exception.BusinessException;
import ru.freelib.exception.NotFoundException;
import ru.freelib.model.dto.BookDto;
import ru.freelib.model.entity.Author;
import ru.freelib.model.entity.Book;
import ru.freelib.model.entity.Genre;
import ru.freelib.model.form.BookForm;
import ru.freelib.repository.AuthorRepository;
import ru.freelib.repository.BookRepository;
import ru.freelib.repository.GenreRepository;
import ru.freelib.util.FileStorageUtil;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final RecommendationService recommendationService;
    private final FileStorageUtil fileStorage;

    @Transactional
    public Book createBook(BookForm form, MultipartFile file, Long authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Автор", authorId));

        Set<Genre> genres = resolveGenres(form.getGenreIds());
        String filePath = fileStorage.store(file);

        Book book = Book.builder()
                .title(form.getTitle().trim())
                .description(form.getDescription() != null ? form.getDescription().trim() : null)
                .filePath(filePath)
                .author(author)
                .genres(genres)
                .views(0L)
                .build();

        Book saved = bookRepository.save(book);
        scheduleEmbeddingUpdate(saved);
        return saved;
    }

    @Transactional
    public Book updateBook(Long bookId, BookForm form, MultipartFile file) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Книга", bookId));

        book.setTitle(form.getTitle().trim());
        book.setDescription(form.getDescription() != null ? form.getDescription().trim() : null);

        if (file != null && !file.isEmpty()) {
            fileStorage.delete(book.getFilePath());
            book.setFilePath(fileStorage.store(file));
        }

        book.setGenres(resolveGenres(form.getGenreIds()));
        Book updated = bookRepository.save(book);
        scheduleEmbeddingUpdate(updated);
        return updated;
    }

    @Transactional
    public void deleteBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Книга", bookId));
        fileStorage.delete(book.getFilePath());
        bookRepository.delete(book);
    }

    public Book getById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Книга", id));
    }

    public List<Book> findByAuthorId(Long authorId) {
        return bookRepository.findByAuthorId(authorId);
    }

    private Set<Genre> resolveGenres(List<Long> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            throw new BusinessException("Необходимо выбрать хотя бы один жанр");
        }
        Set<Genre> genres = genreRepository.findAllById(genreIds).stream().collect(Collectors.toSet());
        if (genres.size() != genreIds.size()) {
            throw new BusinessException("Некоторые жанры не найдены");
        }
        return genres;
    }

    private void scheduleEmbeddingUpdate(Book book) {
        try {
            List<String> genreNames = book.getGenres().stream()
                    .map(Genre::getName)
                    .collect(Collectors.toList());
            recommendationService.updateBookEmbedding(
                    book.getId(), book.getTitle(), book.getAuthor().getNickname(),
                    book.getDescription(), genreNames);
        } catch (Exception e) {
            log.warn("Не удалось обновить эмбеддинг для книги {}: {}", book.getId(), e.getMessage());
        }
    }

    public List<Book> findAll() {
        return bookRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public List<Book> getByGenreId(Long genreId) {
        return bookRepository.findByGenresId(genreId);
    }

    public Page<BookDto> search(String title, String authorName, List<Long> genreIds,
                                Long minViews, Long maxViews, Pageable pageable) {
        Page<Book> booksPage = bookRepository.findByDynamicFilters(title, authorName, genreIds, minViews, maxViews, pageable);

        return booksPage.map(b -> new BookDto(
                b.getId(),
                b.getTitle(),
                b.getDescription(),
                b.getAuthor().getNickname(),
                b.getGenres().stream().map(Genre::getName).toList(),
                b.getViews(),
                b.getFilePath()
        ));
    }

    public List<Book> getLatestBooks() {
        return bookRepository.findTop10ByOrderByCreatedAtDesc();
    }

    public List<Book> getPopularBooks() {
        return bookRepository.findTop10BooksWithViewsAboveAverage();
    }

    @Transactional
    public void incrementViews(Long bookId) {
        bookRepository.incrementViews(bookId);
    }
}