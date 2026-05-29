package ru.freelib.controller.advice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.freelib.model.entity.UserAccount;
import ru.freelib.security.CustomUserDetails;
import ru.freelib.service.UserAccountService;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    @ModelAttribute("currentContext")
    public String currentContext(HttpServletRequest request) {
        return request.getContextPath();
    }

    @Getter
    public static class UserView {
        private final Long id;
        private final String login;
        private final String nickname;
        private final String role;
        private final boolean admin;
        private final boolean author;
        private final boolean reader;

        public UserView(CustomUserDetails d) {
            this.id = d.getId();
            this.login = d.getUsername();
            this.nickname = d.getUserAccount().getAuthor().getNickname();
            this.role = d.getUserAccount().getRole().name();
            this.admin = d.getUserAccount().getRole() == UserAccount.Role.ROLE_ADMIN;
            this.author = d.getUserAccount().getRole() == UserAccount.Role.ROLE_AUTHOR
                    || d.getUserAccount().getRole() == UserAccount.Role.ROLE_ADMIN;
            this.reader = d.getUserAccount().getRole() == UserAccount.Role.ROLE_READER;
        }
    }
}