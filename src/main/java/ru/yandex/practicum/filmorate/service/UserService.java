package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserStorage {

    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<Long> addFriend(User user, User userOther) {
        user.getFriends().add(userOther.getId());
        userOther.getFriends().add(user.getId());
        return user.getFriends();
    }

    public List<Long> deleteFriend(User user, User userOther) {
        user.getFriends().remove(userOther.getId());
        userOther.getFriends().remove(user.getId());
        return user.getFriends();
    }

    public List<User> getCommonFriends(long id, long otherId) {
        List<User> commonUsers = new ArrayList<>();
        getUser(id).getFriends().stream()
                .filter(getUser(otherId).getFriends()::contains)
                .forEach(n -> commonUsers.add(getUser(n)));
        return commonUsers;
    }

    @Override
    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    @Override
    public User getUser(long id) {
        return userStorage.getUser(id);
    }

    @Override
    public User addUser(User newUser) {
        return userStorage.addUser(newUser);
    }

    @Override
    public User updateUser(User newUser) {
        return userStorage.updateUser(newUser);
    }

    @Override
    public List<User> getFriends(long id) {
        return userStorage.getFriends(id);
    }
}
