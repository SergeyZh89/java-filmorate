package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class InMemoryUserStorage implements UserStorage {
    private List<User> users = new ArrayList<>();
    private long idUser;

    private long idUsers() {
        return ++idUser;
    }

    @Override
    public List<User> getUsers() {
        return users;
    }

    @Override
    public User getUser(long id) {
        if(users.stream().filter(user -> user.getId() == id).findFirst().isPresent()){
            return users.stream().filter(user -> user.getId() == id).findFirst().get();
        } else {
            throw new UserNotFoundException("Такого пользователя не существует");
        }
    }

    @Override
    public User addUser(User newUser) {
        newUser.setId(idUsers());
        users.add(newUser);
        return newUser;
    }

    @Override
    public User updUser(User newUser) {
            for (User user : users) {
                if (user.getId() == newUser.getId()) {
                    users.remove(user);
                    users.add(newUser);
                } else throw new UserNotFoundException("Такого пользователя не существует");
            }
            return newUser;
    }
}
