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

    protected boolean validatorUser(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@") || LocalDate.now().isBefore(user.getBirthday())
                || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            return false;
        } else if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
            return true;
        } else {
            return true;
        }
    }

    @GetMapping
    public List<User> getUsers() {
        return users;
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        if (validatorUser(user)) {
            user.setId(idUsers());
            users.add(user);
        } else {
            throw new ValidationException(HttpStatus.BAD_REQUEST);
        }
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        for (User user1 : users) {
            if (user1.getId() == user.getId()) {
                users.remove(user1);
                users.add(user);
            } else {
                throw new ValidationException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return user;
    }
}