package ru.freelib.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.freelib.model.entity.Comment;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBookIdOrderByCreatedAtDesc(Long bookId);
    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);
}