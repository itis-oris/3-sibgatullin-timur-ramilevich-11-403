package ru.freelib.model.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterForm {
    @NotBlank(message = "Логин обязателен")
    @Size(min = 3, max = 50)
    private String login;

    @NotBlank(message = "Пароль обязателен")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{16,}$",
            message = "Пароль: 16+ символов, строчная, прописная, цифра")
    private String password;

    @NotBlank(message = "Подтвердите пароль")
    private String passwordConfirm;

    @NotBlank(message = "Выберите роль")
    private String role;

    @NotBlank(message = "Никнейм обязателен")
    @Size(max = 100)
    private String nickname;

    @Size(max = 1000)
    private String description;

    public boolean isPasswordMatching() {
        return password != null && password.equals(passwordConfirm);
    }
}