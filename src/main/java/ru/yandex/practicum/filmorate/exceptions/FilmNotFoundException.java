package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FilmNotFoundException extends RuntimeException {

    public FilmNotFoundException(String message) {
        super(message);
    }

    @RestControllerAdvice
    public static class GlobalExceptionHandler {

        @ExceptionHandler({UserNotFoundException.class, FilmNotFoundException.class, GenreNotFoundException.class, RatindMpaNotFoundException.class})
        public void handleNotFound(HttpServletResponse response) throws IOException {
            response.sendError(HttpStatus.NOT_FOUND.value());
        }

        @ExceptionHandler({ValidationException.class})
        public void handlerBadValidation(HttpServletResponse response) throws IOException {
            response.sendError(HttpStatus.BAD_REQUEST.value());
        }
    }
}
