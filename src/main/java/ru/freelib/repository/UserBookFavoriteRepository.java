package ru.freelib.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.freelib.model.entity.UserBookFavorite;
import java.util.List;
import java.util.Optional;

public interface UserBookFavoriteRepository extends JpaRepository<UserBookFavorite, Long> {

    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    @Modifying
    @Query("DELETE FROM UserBookFavorite f WHERE f.user.id = :userId AND f.book.id = :bookId")
    int deleteByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

    List<UserBookFavorite> findByUserIdOrderByAddedAtDesc(Long userId);
}