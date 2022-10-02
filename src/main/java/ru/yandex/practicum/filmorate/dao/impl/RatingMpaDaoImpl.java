package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.RatingMpaDao;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.List;

@Slf4j
@Component
public class RatingMpaDaoImpl implements RatingMpaDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public RatingMpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<RatingMpa> getRatings() {
        return jdbcTemplate.query("SELECT * FROM MPA_RATINGS", new BeanPropertyRowMapper<>(RatingMpa.class));
    }

    @Override
    public RatingMpa getRatingsById(int id) {
        return jdbcTemplate.query("SELECT * FROM MPA_RATINGS WHERE ID = ?", new BeanPropertyRowMapper<>(RatingMpa.class), id)
                .stream().findAny().orElse(null);
    }
}
