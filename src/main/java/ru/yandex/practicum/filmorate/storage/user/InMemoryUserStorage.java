package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Long, User> users = new HashMap();
    private long idUser;

    private long idUsers() {
        return ++idUser;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public List<User> getFriends(long id) {
        List<User> userList = new ArrayList<>();
        if (users.containsKey(id)) {
            for (Long value : users.get(id).getFriends()) {
                userList.add(users.get(value));
            }
            return userList;
        } else {
            throw new UserNotFoundException("Такого пользователя не существует");
        }
    }

    @Override
    public User getUser(long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new UserNotFoundException("Такого пользователя не существует");
        }
    }

    @Override
    public User addUser(User newUser) {
        newUser.setId(idUsers());
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public User updateUser(User newUser) {
        if (users.containsKey(newUser.getId())) {
            users.remove(newUser.getId());
            users.put(newUser.getId(), newUser);
            return newUser;
        } else {
            throw new UserNotFoundException("Такого пользователя не существует");
        }
    }
}
