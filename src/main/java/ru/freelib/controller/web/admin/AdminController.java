package ru.freelib.controller.web.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.freelib.service.AuthorService;
import ru.freelib.service.BookService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthorService authorService;
    private final BookService bookService;

    @GetMapping
    public String panel() {
        return "admin/panel";
    }

    @PostMapping
    public String lookupAuthor(@RequestParam String author, RedirectAttributes redirectAttributes) {
        var found = authorService.findByNicknamePartial(author.trim());

        if (found == null) {
            redirectAttributes.addFlashAttribute("errormessage", "Автор не найден!");
            return "redirect:/admin";
        }
        return "redirect:/admin/edit-author?id=" + found.getId();
    }
}