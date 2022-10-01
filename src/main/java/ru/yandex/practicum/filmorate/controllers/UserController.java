package ru.yandex.practicum.filmorate.controllers;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@NoArgsConstructor
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        log.debug("Получен запрос на список пользователей");
        return userService.getUsers();
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable long id) {
        log.debug("Получен запрос на список друзей пользователя: id " + id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}")
    public Optional<User> getUser(@PathVariable long id) {
        log.debug("Получен запрос на пользователя под номером: id " + id);
        return userService.getUser(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.debug("Получен запрос на список общих друзей пользователя: id " + id + " с пользователем: id " + otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public List<Long> addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.debug("Получен запрос на добавление в друзья пользователю: id " + id + " от пользователя: id " + friendId);
        return userService.addFriend(userService.getUser(id).get(), userService.getUser(friendId).get());
    }

    @PostMapping
    public Optional<User> createUser(@RequestBody User newUser) {
        log.debug("Получен запрос на добавление пользователя: id " + newUser.getId());
        validatorUser(newUser);
        return userService.addUser(newUser);
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        log.debug("Получен запрос на обновление пользователя: id " + newUser.getId());
        if (newUser.getId() < 0) {
            throw new UserNotFoundException("Такого пользователя не существует");
        } else return userService.updateUser(newUser);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public List<Long> deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        log.debug("Получен запрос на удаление из друзей пользователя: id " + id + " от пользователя: id " + friendId);
        return userService.deleteFriend(userService.getUser(id).get(), userService.getUser(friendId).get());
    }

    @DeleteMapping("{userId}")
    public void deleteUser (@PathVariable long userId){
        log.debug("Получен запрос на удаление пользователя " + userId);
        userService.deleteUser(userId);
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