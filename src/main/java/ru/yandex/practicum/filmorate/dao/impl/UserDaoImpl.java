package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserDaoImpl implements UserDao {
    private final JdbcTemplate jdbcTemplate;

    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getUsers() {
        String sql = "SELECT * FROM USERS";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public List<User> getFriends(long id) {
        String sql = "SELECT * FROM USERS WHERE ID IN (SELECT FRIENDS_ID FROM USER_FRIENDS WHERE USER_ID=?)";
        getUser(id);
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class), id);
    }

    @Override
    public List<Long> addFriend(User user, User otherUser) {
        String sqlInsert = "INSERT INTO USER_FRIENDS VALUES (?,?)";
        String sqlDelete = "SELECT friends_id FROM USER_FRIENDS WHERE user_id=?";
        jdbcTemplate.update(sqlInsert, user.getId(), otherUser.getId());
        return jdbcTemplate.query(sqlDelete, rs -> {
            List<Long> list = new ArrayList<>();
            while (rs.next()) {
                list.add(rs.getLong("friends_id"));
            }
            return list;
        }, user.getId());
    }

    @Override
    public List<User> getCommonFriends(long id, long otherId) {
        List<User> userList = new ArrayList<>();
        List<Long> commonList;
        List<Long> friendListUser = jdbcTemplate.query("SELECT FRIENDS_ID FROM USER_FRIENDS WHERE USER_ID=?", rs -> {
            List<Long> list = new ArrayList<>();
            while (rs.next()) {
                list.add(rs.getLong("friends_id"));
            }
            return list;
        }, id);
        List<Long> friendListOtherUser = jdbcTemplate.query("SELECT FRIENDS_ID FROM USER_FRIENDS WHERE USER_ID=?", rs -> {
            List<Long> list = new ArrayList<>();
            while (rs.next()) {
                list.add(rs.getLong("friends_id"));
            }
            return list;
        }, otherId);
        commonList = friendListUser.stream().filter(friendListOtherUser::contains).collect(Collectors.toList());
        commonList.forEach(x -> userList.add(getUser(x).get()));
        return userList;
    }

    @Override
    public List<Long> deleteFriend(User user, User userOther) {
        jdbcTemplate.update("DELETE FROM USER_FRIENDS WHERE USER_ID=? AND FRIENDS_ID =?", user.getId(), userOther.getId());
        return jdbcTemplate.query("SELECT FRIENDS_ID FROM USER_FRIENDS WHERE USER_ID=?", rs -> {
            List<Long> userFriends = new ArrayList<>();
            while (rs.next()) {
                userFriends.add(rs.getLong("FRIENDS_ID"));
            }
            return userFriends;
        }, user.getId());
    }

    @Override
    public Optional<User> getUser(long id) {
        return Optional.ofNullable(jdbcTemplate.query("SELECT * FROM USERS WHERE ID=?", new BeanPropertyRowMapper<>(User.class), id)
                .stream()
                .findAny()
                .orElseThrow(() -> new UserNotFoundException("Такого пользователя не существует")));
    }

    @Override
    public Optional<User> addUser(User newUser) {
        String sql = "insert into USERS (name, email, login, birthday) values (?, ?, ?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"ID"});
            ps.setString(1, newUser.getName());
            ps.setString(2, newUser.getEmail());
            ps.setString(3, newUser.getLogin());
            ps.setDate(4, Date.valueOf(newUser.getBirthday()));
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        assert key != null;
        newUser.setId(key.longValue());
        return getUser(newUser.getId());
    }

    @Override
    public User updateUser(User newUser) {
        String sql = "UPDATE USERS SET NAME=?, LOGIN=?, EMAIL=?,BIRTHDAY=? WHERE ID=?";
        jdbcTemplate.update(sql, newUser.getName(),
                newUser.getLogin(),
                newUser.getEmail(),
                newUser.getBirthday(),
                newUser.getId());
        return newUser;
    }

    @Override
    public void deleteUser(long id) {
        String sql = "DELETE FROM USERS WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
