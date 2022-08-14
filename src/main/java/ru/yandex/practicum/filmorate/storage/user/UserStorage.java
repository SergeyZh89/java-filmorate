package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public interface UserStorage {
    List<User> getUsers();

    User getUser(long id);

    User addUser(User newUser);

    User updateUser(User newUser);

    List<User> getFriends(long id);
}
