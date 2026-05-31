# Freelib

Платформа для публикации и чтения книг с AI-генерацией описаний и векторным поиском похожих произведений.

## Стек

**Backend:**
- Java 17, Spring Boot 3.4.5
- Spring Data JPA + Hibernate 6.6 (работа с PostgreSQL)
- Spring Security + JWT (аутентификация через HttpOnly-куки)
- Flyway (миграции БД)
- Lombok

**Внешние сервисы:**
- PostgreSQL — основное хранилище данных + расширение `pgvector` для векторного поиска
- Redis — кэширование жанров и авторов
- MinIO — объектное хранилище для файлов книг (PDF, EPUB, FB2 и т.д.)
- llama.cpp — инференс локальных LLM для генерации и улучшения описаний книг (`qwen3.5:4B`) и эмбеддингов (`qwen3-embedding:0.6B`)

**Frontend:**
- Freemarker (серверный рендеринг шаблонов)
- JS (поиск, избранное, AI-генерация)

## Запуск

```
docker compose up -d
mvn spring-boot:run
```

Приложение стартует на `http://localhost:8080`.  
Админка: логин и пароль указать в `.env.example`.  
Redis GUI: `http://localhost:8081`.
