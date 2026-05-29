package ru.freelib.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.freelib.exception.NotFoundException;
import ru.freelib.model.entity.Book;
import ru.freelib.model.entity.Comment;
import ru.freelib.model.entity.UserAccount;
import ru.freelib.model.form.CommentForm;
import ru.freelib.repository.BookRepository;
import ru.freelib.repository.CommentRepository;
import ru.freelib.repository.UserAccountRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional
    public Comment addComment(CommentForm form, Long userId) {
        Book book = bookRepository.findById(form.getBookId())
                .orElseThrow(() -> new NotFoundException("Книга", form.getBookId()));
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь" , userId));

        Comment comment = Comment.builder()
                .book(book)
                .user(user)
                .text(form.getText().trim())
                .build();
        return commentRepository.save(comment);
    }

    public List<Comment> getByBookId(Long bookId) {
        return commentRepository.findByBookIdOrderByCreatedAtDesc(bookId);
    }

    public List<Comment> getByUserId(Long userId) {
        return commentRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}