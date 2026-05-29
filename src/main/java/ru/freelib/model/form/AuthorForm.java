package ru.freelib.model.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthorForm {
    private Long id;

    @NotBlank(message = "Имя автора обязательно")
    @Size(max = 100)
    private String nickname;

    @Size(max = 2000)
    private String bio;
}