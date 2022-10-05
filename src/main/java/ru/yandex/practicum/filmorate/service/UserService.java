package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.RecommendationsDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserDao userDao;
    private final RecommendationsDao recommendationsDao;

    public List<Long> addFriend(User user, User userOther) {
        return userDao.addFriend(user, userOther);
    }

    public List<Long> deleteFriend(User user, User userOther) {
        return userDao.deleteFriend(user, userOther);
    }

    public List<User> getCommonFriends(long id, long otherId) {
        return userDao.getCommonFriends(id, otherId);
    }

    public List<User> getUsers() {
        return userDao.getUsers();
    }

    public Optional<User> getUser(long id) {
        if (id <= 0) {
            throw new UserNotFoundException("Пользователя с таким id не существует.");
        }
        return userDao.getUser(id);
    }

    public Optional<User> addUser(User newUser) {
        return userDao.addUser(newUser);
    }

    public User updateUser(User newUser) {
        if (userDao.getUser(newUser.getId()).isEmpty()) {
            throw new UserNotFoundException("Пользователя с таким id не существует.");
        }
        return userDao.updateUser(newUser);
    }

    public List<User> getFriends(long id) {
        userDao.getUser(id);
        return userDao.getFriends(id);
    }

    public void deleteUser(long id) {
        if (id <= 0) {
            throw new UserNotFoundException("Пользователя с таким id не существует.");
        }
        userDao.deleteUser(id);
    }

    public List<Film> getRecommendationsByUser(long userId, int recCount) {
        return recommendationsDao.getRecommendationsByUser(userId, recCount);
    }
}
