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
import ru.freelib.model.entity.Author;
import ru.freelib.model.entity.UserAccount;
import ru.freelib.model.form.ProfileEditForm;
import ru.freelib.security.CustomUserDetails;
import ru.freelib.service.*;
import ru.freelib.service.IdempotencyService;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserAccountService userAccountService;
    private final BookService bookService;
    private final CommentService commentService;
    private final FavoriteService favoriteService;
    private final IdempotencyService idempotencyService;
    private final AuthorService authorService;

    @GetMapping("/profile")
    public String myProfile(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        model.addAttribute("myFavoriteBooks", favoriteService.getFavorites(user.getId()));
        model.addAttribute("myBooks", bookService.findByAuthorId(user.getUserAccount().getAuthor().getId()));
        model.addAttribute("myComments", commentService.getByUserId(user.getId()));
        return "profile";
    }

    @GetMapping("/profile-edit")
    public String editProfilePage(Model model, @AuthenticationPrincipal CustomUserDetails user, HttpSession session) {
        Author author = user.getUserAccount().getAuthor();
        ProfileEditForm form = new ProfileEditForm();
        form.setNickname(author.getNickname());
        form.setDescription(author.getBio());
        model.addAttribute("form", form);
        model.addAttribute("myBooks", bookService.findByAuthorId(author.getId()));
        model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
        return "profile-edit";
    }

    @PostMapping("/profile-edit")
    public String updateProfile(@Valid @ModelAttribute("form") ProfileEditForm form,
                                BindingResult bindingResult,
                                @RequestParam(value = "delete", required = false) String delete,
                                @RequestParam("idempotencyToken") String token,
                                @AuthenticationPrincipal CustomUserDetails user,
                                HttpSession session,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (!idempotencyService.validateAndConsume(session, token)) {
            redirectAttributes.addFlashAttribute("error", "Повторная отправка формы");
            return "redirect:/profile-edit";
        }

        if ("true".equals(delete)) {
            userAccountService.deleteAccount(user.getId());
            return "redirect:/auth/logout";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
            model.addAttribute("myBooks", bookService.findByAuthorId(user.getUserAccount().getAuthor().getId()));
            return "profile-edit";
        }

        userAccountService.updateProfile(user.getId(), form);
        redirectAttributes.addFlashAttribute("success", "Профиль обновлён");
        return "redirect:/home";
    }

    @GetMapping("/user")
    public String publicProfile(@RequestParam Long id, Model model) {
        var account = userAccountService.getById(id);
        var author = account.getAuthor();
        model.addAttribute("anotherUser", account);
        model.addAttribute("authorBooks", bookService.findByAuthorId(author.getId()));
        model.addAttribute("userComments",
                commentService.getByUserId(id));
        boolean isAuthorOrAdmin = account.getRole() == UserAccount.Role.ROLE_AUTHOR
                || account.getRole() == UserAccount.Role.ROLE_ADMIN;
        model.addAttribute("isAuthorOrAdmin", isAuthorOrAdmin);
        return "public-profile";

    }
}