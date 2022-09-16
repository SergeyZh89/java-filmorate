package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.impl.RatingMpaDaoImpl;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class FilmMapper implements RowMapper<Film> {

    private final JdbcTemplate jdbcTemplate;
    private final RatingMpaDaoImpl ratingMpaDao;

    public FilmMapper(JdbcTemplate jdbcTemplate, RatingMpaDaoImpl ratingMpaDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.ratingMpaDao = ratingMpaDao;
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        RatingMpa ratingMpa;
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setReleaseDate(rs.getDate("release_Date").toLocalDate());
        film.setDescription(rs.getString("description"));
        film.setDuration(rs.getInt("duration"));
        if (rs.getInt("mpa") > 0) {
            ratingMpa = ratingMpaDao.getRatingsById(rs.getInt("mpa"));
            film.setMpa(ratingMpa);
        }
        String sql = "SELECT G.NAME, G.ID FROM GENRES AS G JOIN FILM_GENRE AS FG ON G.ID = FG.GENRE_ID WHERE FILM_ID=?";
        List<Genre> genreList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Genre.class), film.getId());
        film.setGenres(genreList);
        return film;
    }
}