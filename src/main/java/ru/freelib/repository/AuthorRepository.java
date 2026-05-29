package ru.freelib.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.freelib.model.entity.Author;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByNickname(String nickname);
    boolean existsByNickname(String nickname);

    @Query("SELECT a FROM Author a LEFT JOIN FETCH a.account WHERE a.id = :id")
    Optional<Author> findByIdWithAccount(@Param("id") Long id);

    List<Author> findByNicknameContainingIgnoreCase(String nicknameFragment);
}
