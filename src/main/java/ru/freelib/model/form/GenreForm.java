package ru.freelib.model.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GenreForm {
    private Long id;

    @NotBlank(message = "Название жанра обязательно")
    @Size(min = 2, max = 50, message = "Название: от 2 до 50 символов")
    private String name;

    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    private String description;
}