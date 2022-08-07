package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private List<User> users = new ArrayList<>();
    private int idUser;

    private int idUsers() {
        return ++idUser;
    }

    protected void validatorUser(User user) {
        if (user.getEmail().isBlank()) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Имя не должно быть пустым");
        } else if (!user.getEmail().contains("@")) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Email должен содержать символ @");
        } else if (LocalDate.now().isBefore(user.getBirthday())) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Дата рождения не должна быть в будущем");
        } else if (user.getLogin().isBlank()) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Логин не должен быть пустым");
        } else if (user.getLogin().contains(" ")) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Логин не должен содержать пробелы");
        } else if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    @GetMapping
    public List<User> getUsers() {
        log.debug("Получен запрос на список пользователей");
        return users;
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.debug("Получен запрос на добавление пользователя");
        validatorUser(user);
        user.setId(idUsers());
        users.add(user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.debug("Получен запрос на обновление пользователя");
        for (User user : users) {
            if (user.getId() == newUser.getId()) {
                users.remove(user);
                users.add(newUser);
            } else {
                throw new ValidationException(HttpStatus.INTERNAL_SERVER_ERROR, "Такого пользователя нет");
            }
        }
        return newUser;
    }
}