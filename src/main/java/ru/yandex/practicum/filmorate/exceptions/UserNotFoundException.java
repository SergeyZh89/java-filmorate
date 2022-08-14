package ru.yandex.practicum.filmorate.exceptions;

public class UserNotFoundException extends IllegalArgumentException{
    public UserNotFoundException(String message) {
        super(message);
    }
}
