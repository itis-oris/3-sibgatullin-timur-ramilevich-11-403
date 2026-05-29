package ru.freelib.controller.web;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.freelib.model.form.LoginForm;
import ru.freelib.model.form.RegisterForm;
import ru.freelib.service.AuthService;
import ru.freelib.service.IdempotencyService;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final IdempotencyService idempotencyService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("form", new LoginForm());
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("form") LoginForm form,
                        BindingResult bindingResult,
                        HttpServletResponse response,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/login";
        }
        try {
            authService.login(form, response);
            return "redirect:/home";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Неверный логин или пароль");
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model, HttpSession session) {
        model.addAttribute("form", new RegisterForm());
        model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
        return "auth/registration";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisterForm form,
                           BindingResult bindingResult,
                           HttpSession session,
                           @RequestParam(value = "idempotencyToken", required = false) String token,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (token != null && !idempotencyService.validateAndConsume(session, token)) {
            redirectAttributes.addFlashAttribute("error", "Форма уже отправлена");
            return "redirect:/auth/register";
        }

        Map<String, String> fieldErrors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(e ->
                fieldErrors.putIfAbsent(e.getField(), e.getDefaultMessage())
        );

        boolean hasFormErrors = bindingResult.hasErrors() || !form.isPasswordMatching();
        if (!form.isPasswordMatching()) {
            fieldErrors.putIfAbsent("passwordConfirm", "Пароли не совпадают");
        }

        if (hasFormErrors) {
            return renderRegistrationForm(model, session, fieldErrors, null);
        }

        try {
            authService.register(form);
            redirectAttributes.addFlashAttribute("success", "Регистрация успешна. Войдите в систему.");
            return "redirect:/auth/login";
        } catch (ru.freelib.exception.BusinessException e) {
            String message = e.getMessage();
            if (message.contains("Логин")) {
                fieldErrors.put("login", message);
            } else if (message.contains("Никнейм")) {
                fieldErrors.put("nickname", message);
            } else {
                model.addAttribute("error", message);
            }
            return renderRegistrationForm(model, session, fieldErrors, null);
        }
    }


    private String renderRegistrationForm(Model model, HttpSession session,
                                          Map<String, String> fieldErrors, String errorMessage) {
        model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
        model.addAttribute("fieldErrors", fieldErrors != null ? fieldErrors : Map.of());
        if (errorMessage != null) {
            model.addAttribute("error", errorMessage);
        }
        return "auth/registration";
    }

    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(HttpServletResponse response) {
        authService.logout(response);
        return "redirect:/home";
    }
}