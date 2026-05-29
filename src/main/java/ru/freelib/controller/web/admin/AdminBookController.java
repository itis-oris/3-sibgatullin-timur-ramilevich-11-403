package ru.freelib.controller.web.admin;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.freelib.model.form.BookForm;
import ru.freelib.service.AuthorService;
import ru.freelib.service.BookService;
import ru.freelib.service.GenreService;
import ru.freelib.service.IdempotencyService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminBookController {

    private final AuthorService authorService;
    private final BookService bookService;
    private final GenreService genreService;
    private final IdempotencyService idempotencyService;

    @GetMapping("/upload")
    public String uploadPage(@RequestParam Long id, Model model, HttpSession session) {
        var author = authorService.getById(id);
        model.addAttribute("form", new BookForm());
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("authorId", id);
        model.addAttribute("authorNickname", author.getNickname());
        model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
        return "admin/book-upload";
    }

    @PostMapping("/upload")
    public String uploadBook(@RequestParam Long authorId,
                             @Valid @ModelAttribute("form") BookForm form,
                             BindingResult bindingResult,
                             @RequestParam("file") MultipartFile file,
                             @RequestParam("idempotencyToken") String token,
                             HttpSession session,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (!idempotencyService.validateAndConsume(session, token)) {
            redirectAttributes.addFlashAttribute("error", "Форма уже отправлена");
            return "redirect:/admin/upload?id=" + authorId;
        }

        if (bindingResult.hasErrors() || file.isEmpty()) {
            model.addAttribute("genres", genreService.findAll());
            model.addAttribute("authorId", authorId);
            model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
            if (file.isEmpty()) model.addAttribute("error", "Файл обязателен");
            return "admin/book-upload";
        }

        try {
            bookService.createBook(form, file, authorId);
            redirectAttributes.addFlashAttribute("success", "Книга успешно загружена");
            return "redirect:/admin/edit-author?id=" + authorId;
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("genres", genreService.findAll());
            model.addAttribute("authorId", authorId);
            model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
            return "admin/book-upload";
        }
    }

    @GetMapping("/edit-book")
    public String editBookPage(@RequestParam Long id, Model model, HttpSession session) {
        var book = bookService.getById(id);
        BookForm form = new BookForm();
        form.setId(book.getId());
        form.setTitle(book.getTitle());
        form.setDescription(book.getDescription());
        form.setGenreIds(book.getGenres().stream().map(g -> g.getId()).toList());

        model.addAttribute("form", form);
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("authorId", book.getAuthor().getId());
        model.addAttribute("authorNickname", book.getAuthor().getNickname());
        model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
        return "admin/book-edit";
    }

    @PostMapping("/edit-book")
    public String updateBook(@RequestParam Long id,
                             @Valid @ModelAttribute("form") BookForm form,
                             BindingResult bindingResult,
                             @RequestParam(value = "file", required = false) MultipartFile file,
                             @RequestParam(value = "delete", required = false) String delete,
                             @RequestParam("idempotencyToken") String token,
                             HttpSession session,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        var book = bookService.getById(id);
        Long authorId = book.getAuthor().getId();

        if (!idempotencyService.validateAndConsume(session, token)) {
            redirectAttributes.addFlashAttribute("error", "Повторная отправка");
            return "redirect:/admin/edit-book?id=" + id;
        }

        if ("true".equals(delete)) {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("success", "Книга удалена");
            return "redirect:/admin/edit-author?id=" + authorId;
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("genres", genreService.findAll());
            model.addAttribute("authorId", authorId);
            model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
            return "admin/book-edit";
        }

        try {
            bookService.updateBook(id, form, file);
            redirectAttributes.addFlashAttribute("success", "Книга обновлена");
            return "redirect:/admin/edit-author?id=" + authorId;
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("genres", genreService.findAll());
            model.addAttribute("authorId", authorId);
            model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
            return "admin/book-edit";
        }
    }
}