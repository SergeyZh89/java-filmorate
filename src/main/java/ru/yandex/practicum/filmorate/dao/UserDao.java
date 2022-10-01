package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Component
public interface UserDao {
    List<User> getUsers();

    Optional<User> getUser(long id);

    Optional<User> addUser(User newUser);

    User updateUser(User newUser);

    List<User> getFriends(long id);

    List<Long> addFriend(User user, User otherUser);

    List<User> getCommonFriends(long id, long otherId);

    List<Long> deleteFriend(User user, User userOther);

    void deleteUser(long id);
}

