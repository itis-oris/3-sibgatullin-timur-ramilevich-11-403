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
import ru.freelib.model.entity.Author;
import ru.freelib.model.form.AuthorForm;
import ru.freelib.repository.AuthorRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorService {

    private final AuthorRepository authorRepository;

    public Author getById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Автор", id));
    }

    @Cacheable(value = "authors", key = "'all'")
    public List<Author> findAll() {
        return authorRepository.findAll(Sort.by(Sort.Direction.ASC, "nickname"));
    }

    @Transactional
    @CacheEvict(value = "authors", key = "'all'")
    public Author create(AuthorForm form) {
        if (authorRepository.existsByNickname(form.getNickname())) {
            throw new DuplicateException("Автор с таким никнеймом уже существует");
        }
        Author author = Author.builder()
                .nickname(form.getNickname().trim())
                .bio(form.getBio() != null ? form.getBio().trim() : null)
                .build();
        return authorRepository.save(author);
    }

    @Transactional
    @CachePut(value = "authors", key = "#id")
    @CacheEvict(value = "authors", key = "'all'")
    public Author update(Long id, AuthorForm form) {
        Author author = getById(id);
        author.setNickname(form.getNickname().trim());
        author.setBio(form.getBio() != null ? form.getBio().trim() : null);
        return authorRepository.save(author);
    }

    @Transactional
    @CacheEvict(value = "authors", allEntries = true)
    public void delete(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new NotFoundException("Автор", id);
        }
        authorRepository.deleteById(id);
    }


    public Author getByIdWithAccount(Long id) {
        return authorRepository.findByIdWithAccount(id)
                .orElseThrow(() -> new NotFoundException("Автор", id));
    }

    public Author findByNicknamePartial(String nicknameFragment) {
        if (nicknameFragment == null || nicknameFragment.isBlank()) {
            return null;
        }

        var exact = authorRepository.findByNickname(nicknameFragment.trim());
        if (exact.isPresent()) {
            return exact.get();
        }

        return authorRepository.findByNicknameContainingIgnoreCase(nicknameFragment.trim())
                .stream()
                .findFirst()
                .orElse(null);
    }
}