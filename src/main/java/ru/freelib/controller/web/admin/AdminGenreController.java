package ru.freelib.controller.web.admin;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.freelib.model.form.GenreForm;
import ru.freelib.service.GenreService;
import ru.freelib.service.IdempotencyService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminGenreController {

    private final GenreService genreService;
    private final IdempotencyService idempotencyService;

    @GetMapping("/all-genres")
    public String allGenres(Model model) {
        model.addAttribute("genres", genreService.findAll());
        return "admin/genres";
    }

    @GetMapping("/create-genre")
    public String createGenrePage(Model model, HttpSession session) {
        model.addAttribute("form", new GenreForm());
        model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
        return "admin/genre-create";
    }

    @PostMapping("/create-genre")
    public String createGenre(@Valid @ModelAttribute("form") GenreForm form,
                              BindingResult bindingResult,
                              @RequestParam("idempotencyToken") String token,
                              HttpSession session,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (!idempotencyService.validateAndConsume(session, token)) {
            redirectAttributes.addFlashAttribute("error", "Повторная отправка");
            return "redirect:/admin/create-genre";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
            return "admin/genre-create";
        }
        try {
            genreService.create(form);
            redirectAttributes.addFlashAttribute("success", "Жанр создан");
            return "redirect:/admin/all-genres";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
            return "admin/genre-create";
        }
    }

    @GetMapping("/edit-genre")
    public String editGenrePage(@RequestParam Long id, Model model, HttpSession session) {
        var genre = genreService.getById(id);
        GenreForm form = new GenreForm();
        form.setId(genre.getId());
        form.setName(genre.getName());
        form.setDescription(genre.getDescription());
        model.addAttribute("form", form);
        model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
        return "admin/genre-edit";
    }

    @PostMapping("/edit-genre")
    public String updateGenre(@RequestParam Long id,
                              @Valid @ModelAttribute("form") GenreForm form,
                              BindingResult bindingResult,
                              @RequestParam(value = "delete", required = false) String delete,
                              @RequestParam("idempotencyToken") String token,
                              HttpSession session,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (!idempotencyService.validateAndConsume(session, token)) {
            redirectAttributes.addFlashAttribute("error", "Повторная отправка");
            return "redirect:/admin/edit-genre?id=" + id;
        }
        if ("true".equals(delete)) {
            genreService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Жанр удалён");
            return "redirect:/admin/all-genres";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
            return "admin/genre-edit";
        }
        try {
            genreService.update(id, form);
            redirectAttributes.addFlashAttribute("success", "Жанр обновлён");
            return "redirect:/admin/all-genres";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("idempotencyToken", idempotencyService.generateToken(session));
            return "admin/genre-edit";
        }
    }
}