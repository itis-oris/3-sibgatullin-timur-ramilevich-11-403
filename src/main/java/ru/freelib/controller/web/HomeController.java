package ru.freelib.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.freelib.service.BookService;
import ru.freelib.service.GenreService;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final GenreService genreService;
    private final BookService bookService;

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("books", bookService.getLatestBooks());
        model.addAttribute("popularBooks", bookService.getPopularBooks());
        return "home";
    }
}