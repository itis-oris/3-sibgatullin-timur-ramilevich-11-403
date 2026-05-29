package ru.freelib.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.freelib.exception.BusinessException;
import ru.freelib.exception.NotFoundException;
import ru.freelib.model.entity.UserAccount;
import ru.freelib.model.form.ProfileEditForm;
import ru.freelib.repository.AuthorRepository;
import ru.freelib.repository.CommentRepository;
import ru.freelib.repository.UserAccountRepository;
import ru.freelib.repository.UserBookFavoriteRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorRepository authorRepository;
    private final CommentRepository commentRepository;
    private final UserBookFavoriteRepository favoriteRepository;

    public UserAccount getById(Long id) {
        return userAccountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Аккаунт не найден", id));
    }

    @Transactional
    public UserAccount updateProfile(Long id, ProfileEditForm form) {
        UserAccount account = getById(id);
        account.getAuthor().setNickname(form.getNickname().trim());
        account.getAuthor().setBio(form.getDescription() != null ? form.getDescription().trim() : null);
        return userAccountRepository.save(account);
    }

    @Transactional
    public void changePassword(Long id, String oldPassword, String newPassword) {
        UserAccount account = getById(id);
        if (!passwordEncoder.matches(oldPassword, account.getPasswordHash())) {
            throw new BusinessException("Текущий пароль неверен");
        }
        account.setPasswordHash(passwordEncoder.encode(newPassword));
        userAccountRepository.save(account);
    }

    @Transactional
    public void deleteAccount(Long id) {
        if (!userAccountRepository.existsById(id)) {
            throw new NotFoundException("Аккаунт", id);
        }

        UserAccount account = userAccountRepository.findById(id).orElseThrow();

        if (account.getAuthor() != null) {
            var author = account.getAuthor();
            account.setAuthor(null);
            userAccountRepository.saveAndFlush(account);
            authorRepository.delete(author);
        }

        userAccountRepository.delete(account);
        userAccountRepository.flush();

        boolean stillExists = userAccountRepository.existsById(id);
        if (stillExists) {
            throw new IllegalStateException("Не удалось удалить аккаунт из БД");
        }
    }
}