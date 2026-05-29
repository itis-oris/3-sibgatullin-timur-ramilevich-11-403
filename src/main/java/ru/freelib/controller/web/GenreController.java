package ru.freelib.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.freelib.service.GenreService;

@Controller
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping("/genres")
    public String allGenres(Model model) {
        model.addAttribute("genres", genreService.findAll());
        return "genres";
    }
}