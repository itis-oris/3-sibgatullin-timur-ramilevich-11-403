package ru.freelib.model.dto;

import java.util.List;

public record BookDto(
        Long id,
        String title,
        String description,
        String authorNickname,
        List<String> genreNames,
        Long views,
        String filePath
) {}