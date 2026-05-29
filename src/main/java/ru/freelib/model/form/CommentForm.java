package ru.freelib.model.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentForm {
    @NotNull(message = "ID книги обязателен")
    private Long bookId;

    @NotBlank(message = "Текст комментария обязателен")
    @Size(max = 2000, message = "Комментарий не должен превышать 2000 символов")
    private String text;
}