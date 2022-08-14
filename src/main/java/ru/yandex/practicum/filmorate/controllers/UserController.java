package ru.yandex.practicum.filmorate.controllers;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@NoArgsConstructor
@RequestMapping("/users")
public class UserController {
    private UserStorage userStorage;
    private UserService userService;

    @Autowired
    public UserController(@Qualifier("inMemoryUserStorage") UserStorage userStorage, @Qualifier("userService") UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        log.debug("Получен запрос на список пользователей");
        return userStorage.getUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable long id) {
        log.debug("Получен запрос на пользователя под номером: " + id);
        return userStorage.getUser(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<Long> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.debug("Получен запрос на список общих друзей пользователя под номером: " + id);
        return userService.getCommonFriends(userStorage.getUser(id), userStorage.getUser(otherId));
    }

    @PostMapping
    public User createUser(@RequestBody User newUser) {
        log.debug("Получен запрос на добавление пользователя: id " + newUser.getId());
        validatorUser(newUser);
        return userStorage.addUser(newUser);
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        log.debug("Получен запрос на обновление пользователя: id " + newUser.getId());
        if (newUser.getId() < 0) {
            throw new UserNotFoundException("Такого пользователя не существует");
        } else return userStorage.updUser(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.debug("Получен запрос на добавление в друзья пользователю: id " + id + " от пользователя: " + friendId);
        return userService.addFriend(userStorage.getUser(id), userStorage.getUser(friendId));
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        log.debug("Получен запрос на удаление из друзей пользователя: id " + id + " от пользователя: " + friendId);
        return userService.deleteFriend(userStorage.getUser(id), userStorage.getUser(friendId));
    }

    @GetMapping("/{id}/friends")
    public Set<Long> getFriends(@PathVariable long id) {
        log.debug("Получен запрос на список друзей пользователя: " + id);
        return userService.getFriends(userStorage.getUsers().stream().filter(user -> user.getId() == id).findFirst().get());
    }

    protected void validatorUser(User user) {
        if (user.getEmail().isBlank()) {
            throw new ValidationException("Имя не должно быть пустым");
        } else if (!user.getEmail().contains("@")) {
            throw new ValidationException("Email должен содержать символ @");
        } else if (LocalDate.now().isBefore(user.getBirthday())) {
            throw new ValidationException("Дата рождения не должна быть в будущем");
        } else if (user.getLogin().isBlank()) {
            throw new ValidationException("Логин не должен быть пустым");
        } else if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не должен содержать пробелы");
        } else if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}