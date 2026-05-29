package ru.freelib.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.freelib.model.entity.Author;
import ru.freelib.model.entity.UserAccount;
import ru.freelib.repository.AuthorRepository;
import ru.freelib.repository.UserAccountRepository;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserAccountRepository userAccountRepository;
    private final AuthorRepository authorRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.login:admin}")
    private String adminLogin;

    @Value("${app.admin.password:changeme}")
    private String adminPassword;

    @Value("${app.admin.nickname:admin}")
    private String adminNickname;

    private static final String ADMIN_BIO = "best admin";

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userAccountRepository.existsByLogin(adminLogin)) {
            log.info("Admin account already exists, skipping initialization");
            return;
        }
        Author adminAuthor = Author.builder()
                .nickname(adminNickname)
                .bio(ADMIN_BIO)
                .createdAt(LocalDateTime.now())
                .build();
        adminAuthor = authorRepository.save(adminAuthor);

        UserAccount admin = UserAccount.builder()
                .login(adminLogin)
                .passwordHash(passwordEncoder.encode(adminPassword))
                .role(UserAccount.Role.ROLE_ADMIN)
                .author(adminAuthor)
                .createdAt(LocalDateTime.now())
                .build();
        userAccountRepository.save(admin);
        log.info("Admin account created successfully");
    }
}