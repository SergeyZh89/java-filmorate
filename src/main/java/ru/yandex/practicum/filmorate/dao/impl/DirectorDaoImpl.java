package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
@Slf4j
@Component
public class DirectorDaoImpl implements DirectorDao {
    private final JdbcTemplate jdbcTemplate;

    public DirectorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> getDirectors() {
        String sql = "SELECT * FROM DIRECTOR";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Director.class));
    }

    @Override
    public Optional<Director> getDirectorById(long id) {
        String sql = "SELECT * FROM DIRECTOR";
        return Optional.ofNullable(jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Director.class))
                .stream()
                .findAny()
                .orElseThrow(() -> new DirectorNotFoundException("Такого директора не существует")));
    }

    @Override
    public Optional<Director> createDirector(Director director) {
        String sql = "INSERT INTO DIRECTOR (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"ID"});
                    ps.setString(1, director.getName());
                    return ps;
                }, keyHolder);
        Number key = keyHolder.getKey();
        assert key != null;
        director.setId(key.longValue());
        return getDirectorById(director.getId());
    }

    @Override
    public Director updateDirector(Director director) {
        String sql = "UPDATE DIRECTOR SET NAME=? WHERE ID=?";
        jdbcTemplate.update(sql, director.getName(),
                director.getId());
        return director;
    }

    @Override
    public void deleteDirector(long id) {
        String sql = "DELETE FROM DIRECTOR WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
