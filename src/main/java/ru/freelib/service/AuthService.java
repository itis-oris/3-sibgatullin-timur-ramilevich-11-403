package ru.freelib.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.freelib.config.JwtConfig;
import ru.freelib.exception.DuplicateException;
import ru.freelib.model.entity.Author;
import ru.freelib.model.entity.UserAccount;
import ru.freelib.model.form.LoginForm;
import ru.freelib.model.form.RegisterForm;
import ru.freelib.repository.AuthorRepository;
import ru.freelib.repository.UserAccountRepository;
import ru.freelib.security.CustomUserDetails;
import ru.freelib.security.JwtTokenProvider;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtProvider;
    private final JwtConfig jwtConfig;
    private final PasswordEncoder passwordEncoder;
    private final UserAccountRepository userAccountRepo;
    private final AuthorRepository authorRepo;

    @Transactional
    public void register(RegisterForm form) {
        if (userAccountRepo.existsByLogin(form.getLogin())) {
            throw new DuplicateException("Логин занят");
        }
        if (authorRepo.existsByNickname(form.getNickname())) {
            throw new DuplicateException("Никнейм занят");
        }

        Author author = Author.builder()
                .nickname(form.getNickname())
                .bio(form.getDescription())
                .createdAt(LocalDateTime.now())
                .build();
        author = authorRepo.save(author);

        UserAccount account = UserAccount.builder()
                .login(form.getLogin())
                .passwordHash(passwordEncoder.encode(form.getPassword()))
                .role(UserAccount.Role.valueOf("ROLE_" + form.getRole().toUpperCase()))
                .author(author)
                .createdAt(LocalDateTime.now())
                .build();
        userAccountRepo.save(account);
    }

    public void login(LoginForm form, HttpServletResponse response) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(form.getLogin(), form.getPassword())
        );
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        String access = jwtProvider.createAccessToken(userDetails.getUsername(), userDetails.getAuthorities());
        String refresh = jwtProvider.createRefreshToken(userDetails.getUsername(), userDetails.getAuthorities());

        setCookie(response, "ACCESS_TOKEN", access, jwtConfig.getAccessTtl());
        setCookie(response, "REFRESH_TOKEN", refresh, jwtConfig.getRefreshTtl());
    }

    public void logout(HttpServletResponse response) {
        clearCookie(response, "ACCESS_TOKEN");
        clearCookie(response, "REFRESH_TOKEN");
    }

    private void setCookie(HttpServletResponse resp, String name, String value, long ttlMs) {
        resp.addHeader("Set-Cookie", String.format(
                "%s=%s; Path=/; Max-Age=%d; HttpOnly; SameSite=Strict%s",
                name, value, ttlMs / 1000, "true".equals(System.getenv("HTTPS")) ? "; Secure" : ""
        ));
    }

    private void clearCookie(HttpServletResponse resp, String name) {
        setCookie(resp, name, "", 0);
    }
}