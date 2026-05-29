package ru.freelib.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.freelib.exception.NotFoundException;
import ru.freelib.model.entity.Book;
import ru.freelib.model.entity.UserAccount;
import ru.freelib.model.entity.UserBookFavorite;
import ru.freelib.repository.BookRepository;
import ru.freelib.repository.UserAccountRepository;
import ru.freelib.repository.UserBookFavoriteRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {

    private final UserBookFavoriteRepository favoriteRepository;
    private final BookRepository bookRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional
    public void add(Long userId, Long bookId) {
        if (favoriteRepository.existsByUserIdAndBookId(userId, bookId)) return;

        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь", userId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Книга", bookId));

        favoriteRepository.save(UserBookFavorite.builder().user(user).book(book).build());
    }

    @Transactional
    public void remove(Long userId, Long bookId) {
        favoriteRepository.deleteByUserIdAndBookId(userId, bookId);
    }

    public boolean exists(Long userId, Long bookId) {
        return favoriteRepository.existsByUserIdAndBookId(userId, bookId);
    }

    public List<UserBookFavorite> getFavorites(Long userId) {
        return favoriteRepository.findByUserIdOrderByAddedAtDesc(userId);
    }
}