package ru.freelib.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.freelib.service.GenreService;

@Controller
@RequiredArgsConstructor
public class SearchController {
    private final GenreService genreService;

    @GetMapping("/search")
    public String searchPage(Model model) {
        model.addAttribute("genres", genreService.findAll());
        return "search";
    }
}