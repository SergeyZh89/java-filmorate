package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    UserController controller;

    @BeforeEach
    void create() {
        controller = new UserController();
    }

    @Test
    void shouldFalseUserEmailIsBlank() {
        User user = new User(1, "", "login", "name", LocalDate.of(1950, 1, 1));
        assertThrows(ValidationException.class, () -> controller.validatorUser(user));
    }

    @Test
    void shouldTrueUserEmailIsNotBlank() {
        User user = new User(1, "email@mail.ru", "login", "name", LocalDate.of(1950, 1, 1));
        assertDoesNotThrow(() -> controller.validatorUser(user));
    }

    @Test
    void shouldFalseUserEmailWithOutDog() {
        User user = new User(1, "email.ru", "login", "name", LocalDate.of(1950, 1, 1));
        assertThrows(ValidationException.class, () -> controller.validatorUser(user));
    }

    @Test
    void shouldTrueUserEmailWithDog() {
        User user = new User(1, "email@mail.ru", "login", "name", LocalDate.of(1950, 1, 1));
        assertDoesNotThrow(() -> controller.validatorUser(user));
    }

    @Test
    void shouldFalseUserLoginIsBlank() {
        User user = new User(1, "email@mail.ru", "", "name", LocalDate.of(1950, 1, 1));
        assertThrows(ValidationException.class, () -> controller.validatorUser(user));
    }

    @Test
    void shouldFalseUserLoginIsBlankAndSpace() {
        User user = new User(1, "email@mail.ru", "login password", "name", LocalDate.of(1950, 1, 1));
        assertThrows(ValidationException.class, () -> controller.validatorUser(user));
    }

    @Test
    void shouldTrueUserLoginIsNotBlank() {
        User user = new User(1, "email@mail.ru", "login", "name", LocalDate.of(1950, 1, 1));
        assertDoesNotThrow(() -> controller.validatorUser(user));
    }

    @Test
    void shouldUserNameisBlankLoginCopyName() {
        User user = new User(1, "email@mail.ru", "login", "", LocalDate.of(1950, 1, 1));
        assertDoesNotThrow(() -> controller.validatorUser(user));
        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void shouldFalseUserBirthdayAfterNow() {
        User user = new User(1, "email@mail.ru", "login", "name", LocalDate.of(2050, 1, 1));
        assertThrows(ValidationException.class, () -> controller.validatorUser(user));
    }
}