package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public interface UserDao {
    List<User> getUsers();

    User getUser(long id);

    User addUser(User newUser);

    User updateUser(User newUser);

    List<User> getFriends(long id);

    List<Long> addFriend(User user, User otherUser);

    List<User> getCommonFriends(long id, long otherId);

    List<Long> deleteFriend(User user, User userOther);
}

