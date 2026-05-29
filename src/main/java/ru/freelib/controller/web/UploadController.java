package ru.freelib.controller.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.freelib.model.form.BookForm;
import ru.freelib.security.CustomUserDetails;
import ru.freelib.service.BookService;
import ru.freelib.service.GenreService;
import ru.freelib.service.IdempotencyService;

@Controller
@RequiredArgsConstructor
public class UploadController {

    private final BookService bookService;
    private final GenreService genreService;
    private final IdempotencyService idempotencyService;

    @GetMapping("/upload")
    public String uploadPage(Model model, HttpSession session) {
        model.addAttribute("form", new BookForm());
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadBook(@Valid @ModelAttribute("form") BookForm form,
                             BindingResult bindingResult,
                             @RequestParam("file") MultipartFile file,
                             @RequestParam("idempotencyToken") String token,
                             @AuthenticationPrincipal CustomUserDetails user,
                             HttpSession session,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (!idempotencyService.validateAndConsume(session, token)) {
            redirectAttributes.addFlashAttribute("error", "Форма уже отправлена");
            return "redirect:/upload";
        }
        if (bindingResult.hasErrors() || file.isEmpty()) {
            model.addAttribute("genres", genreService.findAll());
            model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
            if (file.isEmpty()) model.addAttribute("error", "Файл обязателен");
            return "upload";
        }
        try {
            bookService.createBook(form, file, user.getUserAccount().getAuthor().getId());
            redirectAttributes.addFlashAttribute("success", "Книга успешно загружена");
            return "redirect:/profile";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("genres", genreService.findAll());
            model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
            return "upload";
        }
    }

    @GetMapping("/edit-my-book")
    public String editMyBookPage(@RequestParam Long id,
                                 Model model,
                                 @AuthenticationPrincipal CustomUserDetails user,
                                 HttpSession session) {
        var book = bookService.getById(id);
        if (!book.getAuthor().getId().equals(user.getUserAccount().getAuthor().getId())) {
            return "redirect:/home";
        }
        BookForm form = new BookForm();
        form.setId(book.getId());
        form.setTitle(book.getTitle());
        form.setDescription(book.getDescription());
        form.setGenreIds(book.getGenres().stream().map(g -> g.getId()).toList());

        model.addAttribute("form", form);
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
        return "edit-my-book";
    }

    @PostMapping("/edit-my-book")
    public String updateMyBook(@RequestParam Long id,
                               @Valid @ModelAttribute("form") BookForm form,
                               BindingResult bindingResult,
                               @RequestParam(value = "file", required = false) MultipartFile file,
                               @RequestParam(value = "delete", required = false) String delete,
                               @RequestParam("idempotencyToken") String token,
                               @AuthenticationPrincipal CustomUserDetails user,
                               HttpSession session,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (!idempotencyService.validateAndConsume(session, token)) {
            redirectAttributes.addFlashAttribute("error", "Повторная отправка");
            return "redirect:/edit-my-book?id=" + id;
        }

        if ("true".equals(delete)) {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("success", "Книга удалена");
            return "redirect:/profile";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("genres", genreService.findAll());
            model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
            return "edit-my-book";
        }

        try {
            bookService.updateBook(id, form, file);
            redirectAttributes.addFlashAttribute("success", "Книга обновлена");
            return "redirect:/profile";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("genres", genreService.findAll());
            model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
            return "edit-my-book";
        }
    }
}