package ru.yandex.practicum.filmorate.exceptions;

public class FilmNotFoundException extends IllegalArgumentException{
    public FilmNotFoundException(String message) {
        super(message);
    }
}
