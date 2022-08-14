package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;
@Component
public interface UserStorage {
    List<User> getUsers();
    User getUser(long id);
    User addUser(User newUser);
    User updUser(User newUser);
}
