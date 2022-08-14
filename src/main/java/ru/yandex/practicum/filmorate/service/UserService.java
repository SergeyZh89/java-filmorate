package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    public User addFriend(User newUser, User newUser2) {
        newUser.getFriends().add(newUser2.getId());
        newUser2.getFriends().add(newUser.getId());
        return newUser;
    }

    public User deleteFriend(User newUser, User newUser2) {
        newUser.getFriends().remove(newUser2.getId());
        newUser2.getFriends().remove(newUser.getId());
        return newUser;
    }

    public Set<Long> getFriends(User newUser) {
        return newUser.getFriends();
    }

    public List<Long> getCommonFriends(User user, User otherUser){
        List<Long> commonFriends = new ArrayList<>();
        List<Long> user1 = new ArrayList<>(user.getFriends());
        List<Long> user2 = new ArrayList<>(user.getFriends());
        for (Long aLong : user1) {
            for (Long aLong1 : user2) {
                if (aLong == aLong1) {
                    commonFriends.add(aLong1);
                }
            }
        }
        return commonFriends;
    }
}
