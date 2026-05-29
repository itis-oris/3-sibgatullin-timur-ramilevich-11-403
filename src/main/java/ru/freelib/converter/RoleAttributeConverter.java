package ru.freelib.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.freelib.model.entity.UserAccount;

@Converter()
public class RoleAttributeConverter implements AttributeConverter<UserAccount.Role, String> {

    @Override
    public String convertToDatabaseColumn(UserAccount.Role role) {
        return role == null ? null : role.name();
    }

    @Override
    public UserAccount.Role convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return UserAccount.Role.ROLE_READER;
        }
        try {
            return UserAccount.Role.valueOf(dbData.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Неизвестная роль в БД: " + dbData, e);
        }
    }
}