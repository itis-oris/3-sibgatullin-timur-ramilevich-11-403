package ru.freelib.controller.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.freelib.model.form.CommentForm;
import ru.freelib.security.CustomUserDetails;
import ru.freelib.service.CommentService;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/add")
    public String addComment(@Valid @ModelAttribute CommentForm form,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal CustomUserDetails user,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Ошибка валидации комментария");
            return "redirect:/book?id=" + form.getBookId();
        }
        commentService.addComment(form, user.getId());
        return "redirect:/book?id=" + form.getBookId();
    }
}