package ru.freelib.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.freelib.exception.DuplicateException;
import ru.freelib.exception.NotFoundException;
import ru.freelib.model.entity.Genre;
import ru.freelib.model.form.GenreForm;
import ru.freelib.repository.GenreRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenreService {

    private final GenreRepository genreRepository;

    @Cacheable(value = "genres", key = "#id")
    public Genre getById(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Жанр", id));
    }

    @Cacheable(value = "genres", key = "'all'")
    public List<Genre> findAll() {
        return genreRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    @Transactional
    @CacheEvict(value = "genres", key = "'all'")
    public Genre create(GenreForm form) {
        if (genreRepository.findByName(form.getName()).isPresent()) {
            throw new DuplicateException("Жанр с таким названием уже существует");
        }
        Genre genre = Genre.builder()
                .name(form.getName().trim())
                .description(form.getDescription() != null ? form.getDescription().trim() : null)
                .build();
        return genreRepository.save(genre);
    }

    @Transactional
    @CachePut(value = "genres", key = "#id")
    @CacheEvict(value = "genres", key = "'all'")
    public Genre update(Long id, GenreForm form) {
        Genre genre = getById(id);
        genre.setName(form.getName().trim());
        genre.setDescription(form.getDescription() != null ? form.getDescription().trim() : null);
        return genreRepository.save(genre);
    }

    @Transactional
    @CacheEvict(value = "genres", allEntries = true)
    public void delete(Long id) {
        if (!genreRepository.existsById(id)) {
            throw new NotFoundException("Жанр", id);
        }
        genreRepository.deleteById(id);
    }
}