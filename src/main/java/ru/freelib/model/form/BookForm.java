package ru.freelib.model.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class BookForm {
    private Long id;

    @NotBlank(message = "Название книги обязательно")
    @Size(max = 255)
    private String title;

    @Size(max = 5000, message = "Описание не должно превышать 5000 символов")
    private String description;

    @NotEmpty(message = "Выберите хотя бы один жанр")
    private List<Long> genreIds;
}