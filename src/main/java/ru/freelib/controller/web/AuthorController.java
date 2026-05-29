package ru.freelib.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.freelib.security.CustomUserDetails;
import ru.freelib.service.AuthorService;
import ru.freelib.service.BookService;
import ru.freelib.service.CommentService;
import ru.freelib.service.FavoriteService;

import java.util.Collections;

@Controller
@RequestMapping("/author")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;
    private final BookService bookService;
    private final CommentService commentService;
    private final FavoriteService favoriteService;

    @GetMapping("/{id}")
    public String viewAuthor(@PathVariable Long id,
                             Model model,
                             @AuthenticationPrincipal CustomUserDetails currentUser) {
        var author = authorService.getByIdWithAccount(id);
        model.addAttribute("author", author);
        model.addAttribute("authorBooks", bookService.findByAuthorId(id));
        if (author.getAccount() != null) {
            model.addAttribute("authorComments",
                    commentService.getByUserId(author.getAccount().getId()));
        } else {
            model.addAttribute("authorComments", Collections.emptyList());
        }

        if (currentUser != null) {
            var favBookIds = favoriteService.getFavorites(currentUser.getId()).stream()
                    .map(f -> f.getBook().getId())
                    .toList();
            model.addAttribute("favBookIds", favBookIds);
        }
        return "author/profile";
    }
}