package ru.freelib.model.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileEditForm {
    @NotBlank(message = "Никнейм обязателен")
    @Size(max = 100)
    private String nickname;

    @Size(max = 1000)
    private String description;
}