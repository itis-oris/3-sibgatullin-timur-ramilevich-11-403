package ru.freelib.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.freelib.model.entity.Genre;
import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    Optional<Genre> findByName(String name);
}