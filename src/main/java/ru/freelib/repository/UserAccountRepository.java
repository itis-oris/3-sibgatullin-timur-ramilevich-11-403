package ru.freelib.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.freelib.model.entity.UserAccount;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByLogin(String login);
    boolean existsByLogin(String login);
}