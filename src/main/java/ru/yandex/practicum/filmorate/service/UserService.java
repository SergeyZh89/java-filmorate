package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.UserDaoImpl;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserDaoImpl userDao;

    @Autowired
    public UserService(UserDaoImpl userDao) {
        this.userDao = userDao;
    }

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
            throw new UserNotFoundException("Такого пользователя не существует");
        }
        return userDao.getUser(id);
    }

    public Optional<User> addUser(User newUser) {
        return userDao.addUser(newUser);
    }

    public User updateUser(User newUser) {
        if (userDao.getUser(newUser.getId()) == null) {
            throw new UserNotFoundException("Tакого пользователя не существует");
        }
        return userDao.updateUser(newUser);
    }

    public List<User> getFriends(long id) {
        return userDao.getFriends(id);
    }

    public void deleteUser(long id) {
        if (id <= 0) {
            throw new UserNotFoundException("Tакого пользователя не существует");
        }
        userDao.deleteUser(id);
    }
}
