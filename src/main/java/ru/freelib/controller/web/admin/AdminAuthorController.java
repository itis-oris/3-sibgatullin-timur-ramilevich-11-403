package ru.freelib.controller.web.admin;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.freelib.model.form.AuthorForm;
import ru.freelib.service.AuthorService;
import ru.freelib.service.BookService;
import ru.freelib.service.IdempotencyService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAuthorController {

    private final AuthorService authorService;
    private final BookService bookService;
    private final IdempotencyService idempotencyService;

    @GetMapping("/all-authors")
    public String allAuthors(Model model) {
        model.addAttribute("authors", authorService.findAll());
        return "admin/authors";
    }

    @GetMapping("/create-author")
    public String createAuthorPage(Model model, HttpSession session) {
        model.addAttribute("form", new AuthorForm());
        model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
        return "admin/author-create";
    }

    @PostMapping("/create-author")
    public String createAuthor(@Valid @ModelAttribute("form") AuthorForm form,
                               BindingResult bindingResult,
                               @RequestParam("idempotencyToken") String token,
                               Model model,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!idempotencyService.validateAndConsume(session, token)) {
            redirectAttributes.addFlashAttribute("error", "Повторная отправка");
            return "redirect:/admin/create-author";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
            return "admin/author-create";
        }
        try {
            authorService.create(form);
            redirectAttributes.addFlashAttribute("success", "Автор создан");
            return "redirect:/admin/all-authors";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
            return "admin/author-create";
        }
    }

    @GetMapping("/edit-author")
    public String editAuthorPage(@RequestParam Long id, Model model, HttpSession session) {
        var author = authorService.getById(id);
        AuthorForm form = new AuthorForm();
        form.setId(author.getId());
        form.setNickname(author.getNickname());
        form.setBio(author.getBio());
        model.addAttribute("form", form);
        model.addAttribute("myBooks", bookService.findByAuthorId(id));
        model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
        return "admin/author-edit";
    }

    @PostMapping("/edit-author")
    public String updateAuthor(@RequestParam Long id,
                               @Valid @ModelAttribute("form") AuthorForm form,
                               BindingResult bindingResult,
                               @RequestParam(value = "delete", required = false) String delete,
                               @RequestParam("idempotencyToken") String token,
                               Model model,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!idempotencyService.validateAndConsume(session, token)) {
            redirectAttributes.addFlashAttribute("error", "Повторная отправка");
            return "redirect:/admin/edit-author?id=" + id;
        }
        if ("true".equals(delete)) {
            authorService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Автор удалён");
            return "redirect:/admin/all-authors";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("myBooks", bookService.findByAuthorId(id));
            model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
            return "admin/author-edit";
        }
        try {
            authorService.update(id, form);
            redirectAttributes.addFlashAttribute("success", "Автор обновлён");
            return "redirect:/admin/all-authors";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("myBooks", bookService.findByAuthorId(id));
            model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
            return "admin/author-edit";
        }
    }
}