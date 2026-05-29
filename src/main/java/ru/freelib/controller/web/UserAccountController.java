package ru.freelib.controller.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.freelib.model.form.PasswordChangeForm;
import ru.freelib.security.CustomUserDetails;
import ru.freelib.service.AuthService;
import ru.freelib.service.IdempotencyService;
import ru.freelib.service.UserAccountService;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;
    private final IdempotencyService idempotencyService;
    private final AuthService authService;

    @GetMapping("/settings")
    public String settingsPage(Model model, HttpSession session) {
        model.addAttribute("passwordForm", new PasswordChangeForm());
        model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
        return "account/settings";
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("passwordForm") PasswordChangeForm form,
                                 BindingResult bindingResult,
                                 @RequestParam("idempotencyToken") String token,
                                 HttpSession session,
                                 HttpServletResponse response,
                                 @AuthenticationPrincipal CustomUserDetails user,
                                 RedirectAttributes redirectAttributes) {

        if (!idempotencyService.validateAndConsume(session, token)) {
            redirectAttributes.addFlashAttribute("error", "Форма уже отправлена");
            return "redirect:/account/settings";
        }

        if (bindingResult.hasErrors() || !form.isPasswordMatching()) {
            redirectAttributes.addFlashAttribute("error", "Пароли не совпадают или не соответствуют требованиям");
            return "redirect:/account/settings";
        }

        try {
            userAccountService.changePassword(user.getId(), form.getCurrentPassword(), form.getNewPassword());
            authService.logout(response);
            redirectAttributes.addFlashAttribute("success", "Пароль успешно изменён. Войдите заново.");
            return "redirect:/auth/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/account/settings";
        }
    }

    @PostMapping("/delete")
    public String deleteAccount(@RequestParam("idempotencyToken") String token,
                                HttpSession session,
                                HttpServletResponse response,
                                @AuthenticationPrincipal CustomUserDetails user,
                                RedirectAttributes redirectAttributes) {
        if (!idempotencyService.validateAndConsume(session, token)) {
            redirectAttributes.addFlashAttribute("error", "Повторная отправка формы");
            return "redirect:/account/settings";
        }

        session.invalidate();

        userAccountService.deleteAccount(user.getId());
        authService.logout(response);
        redirectAttributes.addFlashAttribute("success", "Ваш аккаунт удалён");
        return "redirect:/home";
    }
}