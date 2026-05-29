package ru.freelib.controller.advice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import ru.freelib.exception.FreeLibException;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FreeLibException.class)
    public Object handleFreeLibException(FreeLibException ex, HttpServletRequest req) {
        log.warn("Business exception [{}]: {}", ex.getClass().getSimpleName(), ex.getMessage());

        if (isApiRequest(req)) {
            return ResponseEntity.status(ex.getStatus())
                    .body(Map.of("error", ex.getMessage()));
        }
        return errorView(String.valueOf(ex.getStatus().value()),
                ex.getMessage(), ex.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        log.warn("Validation failed: {}", ex.getMessage());
        if (isApiRequest(req)) {
            Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField,
                            FieldError::getDefaultMessage, (a, b) -> a));
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }
        return errorView("400", "Ошибка валидации данных", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Object handle404(NoHandlerFoundException ex, HttpServletRequest req) {
        if (isApiRequest(req)) {
            return ResponseEntity.status(404).body(Map.of("error", "Endpoint not found"));
        }
        return errorView("404", "Страница не найдена", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Object handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                           HttpServletRequest req) {
        log.warn("Method not supported: {} {}", req.getMethod(), req.getRequestURI());
        if (isApiRequest(req)) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body(Map.of("error", "Метод не поддерживается"));
        }
        return new ModelAndView("redirect:/home");
    }

    @ExceptionHandler(Exception.class)
    public Object handleGeneral(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception at {}", req.getRequestURI(), ex);
        if (isApiRequest(req)) {
            return ResponseEntity.status(500).body(Map.of("error", "Внутренняя ошибка сервера"));
        }
        return errorView("500", "Произошла непредвиденная ошибка",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public Object handleTypeMismatch(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex,
                                     HttpServletRequest req) {
        log.warn("Type mismatch: parameter '{}' with value '{}' cannot be converted to {}",
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());

        if (isApiRequest(req)) {
            String message = String.format("Параметр '%s' должен быть типа %s, получено: '%s'",
                    ex.getName(), ex.getRequiredType().getSimpleName(), ex.getValue());
            return ResponseEntity.badRequest().body(Map.of("error", message));
        }
        return errorView("400", "Некорректный формат параметров запроса", HttpStatus.BAD_REQUEST);
    }

    private ModelAndView errorView(String view, String message, HttpStatus status) {
        ModelAndView mav = new ModelAndView("error/" + view);
        mav.addObject("errorMessage", message);
        mav.addObject("statusCode", status.value());
        mav.addObject("currentContext", "");
        mav.setStatus(status);
        return mav;
    }

    private boolean isApiRequest(HttpServletRequest req) {
        String xhr = req.getHeader("X-Requested-With");
        String accept = req.getHeader("Accept");
        return "XMLHttpRequest".equals(xhr) ||
                (accept != null && accept.contains("application/json")
                        && !accept.contains("text/html"));
    }
}