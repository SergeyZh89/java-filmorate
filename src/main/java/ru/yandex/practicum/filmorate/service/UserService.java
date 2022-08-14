package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    public List<Long> addFriend(User user, User userOther) {
        user.getFriends().add(userOther.getId());
        userOther.getFriends().add(user.getId());
        return user.getFriends();
    }

    public List<Long> deleteFriend(User user, User userOther) {
        user.getFriends().remove(userOther.getId());
        userOther.getFriends().remove(user.getFriends());
        return user.getFriends();
    }

    public List<User> getCommonFriends(List<Long> userList, List<Long> userListOther, List<User> list) {
        List<Long> commonId = new ArrayList<>();
        List<User> commonUsers = new ArrayList<>();
        for (Long user : userList) {
            for (Long userOther : userListOther) {
                if (user == userOther) {
                    commonId.add(userOther);
                }
            }
        }
        for (User user : list) {
            for (Long aLong : commonId) {
                if (user.getId() == aLong) {
                    commonUsers.add(user);
                }
            }
        }
        return commonUsers;
    }
}
