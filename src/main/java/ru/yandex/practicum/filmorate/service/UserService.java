package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dao.impl.UserDaoImpl;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDao {
    private final UserDaoImpl userDao;

    @Autowired
    public UserService(UserDaoImpl userDao) {
        this.userDao = userDao;
    }

    @Override
    public List<Long> addFriend(User user, User userOther) {
        return userDao.addFriend(user, userOther);
    }

    @Override
    public List<Long> deleteFriend(User user, User userOther) {
        return userDao.deleteFriend(user, userOther);
    }

    @Override
    public List<User> getCommonFriends(long id, long otherId) {
       return userDao.getCommonFriends(id, otherId);
    }

    @Override
    public List<User> getUsers() {
        return userDao.getUsers();
    }

    @Override
    public Optional<User> getUser(long id) {
        if (id <= 0) {
            throw new UserNotFoundException("Такого пользователя не существует");
        }
        return userDao.getUser(id);
    }

    @Override
    public Optional<User> addUser(User newUser) {
        return userDao.addUser(newUser);
    }

    @Override
    public User updateUser(User newUser) {
        if (userDao.getUser(newUser.getId()) == null) {
            throw new UserNotFoundException("Tакого пользователя не существует");
        }
        return userDao.updateUser(newUser);
    }

    @Override
    public List<User> getFriends(long id) {
        return userDao.getFriends(id);
    }
}
