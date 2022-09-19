package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

@Slf4j
@Component
public class FilmDaoImpl implements FilmDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private List<Genre> genreMapper(int id) {
        String sql = "SELECT G.NAME, G.ID FROM GENRES AS G JOIN FILM_GENRE AS FG ON G.ID = FG.GENRE_ID WHERE FILM_ID=?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Genre.class), id);
    }

    private List<Long> userLikesMapper(int id) {
        String sql = "SELECT USER_ID FROM FILM_LIKES WHERE FILM_ID=?";
        return jdbcTemplate.queryForList(sql, Long.TYPE, id);
    }

    private RatingMpa ratingMpaMapper(int id) {
        String sql = "SELECT * FROM MPA_RATINGS WHERE ID=?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(RatingMpa.class), id).stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT F.ID, F.NAME, F.DESCRIPTION, F.DURATION, F.RELEASE_DATE, MPA, MA.NAME " +
                "FROM FILMS AS F " +
                "JOIN MPA_RATINGS AS MA ON F.MPA = MA.ID";
        return jdbcTemplate.query(sql, rs -> {
            List<Film> filmList = new ArrayList<>();
            while (rs.next()) {
                Film film = new Film().toBuilder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .duration(rs.getInt("duration"))
                        .releaseDate(rs.getDate("release_date").toLocalDate())
                        .mpa(ratingMpaMapper(rs.getInt("MPA")))
                        .genres(genreMapper(rs.getInt("id")))
                        .userLikes(userLikesMapper(rs.getInt("id")))
                        .build();
                filmList.add(film);
            }
            return filmList;
        });

    }

    @Override
    public Film getFilm(long id) {
        String sql = "SELECT F.ID, F.NAME, F.DESCRIPTION, F.DURATION, F.RELEASE_DATE, MPA, MA.NAME FROM FILMS AS F " +
                "JOIN MPA_RATINGS AS MA ON F.MPA = MA.ID where F.ID = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Film().toBuilder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .duration(rs.getInt("duration"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .mpa(ratingMpaMapper(rs.getInt("MPA")))
                .genres(genreMapper(rs.getInt("id")))
                .userLikes(userLikesMapper(rs.getInt("id")))
                .build(), id);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT F.* FROM FILMS AS F " +
                "LEFT JOIN FILM_LIKES FL on F.ID = FL.FILM_ID " +
                "GROUP BY F.ID " +
                "ORDER BY COUNT(FL.FILM_ID) " +
                "DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, rs -> {
            List<Film> filmList = new ArrayList<>();
            while (rs.next()) {
                Film film = new Film().toBuilder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .duration(rs.getInt("duration"))
                        .releaseDate(rs.getDate("release_date").toLocalDate())
                        .mpa(ratingMpaMapper(rs.getInt("MPA")))
                        .genres(genreMapper(rs.getInt("id")))
                        .userLikes(userLikesMapper(rs.getInt("id")))
                        .build();
                filmList.add(film);
            }
            return filmList;
        }, count);
    }

    @Override
    public Film createFilm(Film newFilm) {
        String sql = "INSERT INTO FILMS (name, description, duration, release_date, mpa) VALUES (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"ID"});
                    ps.setString(1, newFilm.getName());
                    ps.setString(2, newFilm.getDescription());
                    ps.setInt(3, newFilm.getDuration());
                    ps.setDate(4, Date.valueOf(newFilm.getReleaseDate()));
                    if (newFilm.getMpa() != null) {
                        ps.setInt(5, newFilm.getMpa().getId());
                    } else {
                        ps.setNull(5, 0);
                    }
                    return ps;
                }, keyHolder);
        Number key = keyHolder.getKey();
        assert key != null;
        newFilm.setId(key.longValue());
        if (newFilm.getGenres() != null) {
            newFilm.getGenres()
                    .forEach(x -> jdbcTemplate.update("INSERT INTO FILM_GENRE VALUES (?,?)", newFilm.getId(), x.getId()));
        }

        return getFilm(newFilm.getId());
    }

    @Override
    public Film updateFilm(Film newFilm) {
        String sqlUpdate = "UPDATE FILMS SET NAME=?, DESCRIPTION=?, DURATION=?,RELEASE_DATE=?,MPA=? WHERE ID=?";
        jdbcTemplate.update(sqlUpdate,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getDuration(),
                newFilm.getReleaseDate(),
                newFilm.getMpa().getId(),
                newFilm.getId());
        String sqlDelete = "DELETE FROM FILM_GENRE WHERE FILM_ID=?";
        jdbcTemplate.update(sqlDelete, newFilm.getId());
        if (!newFilm.getGenres().isEmpty()) {
            String sqlInsert = "INSERT INTO FILM_GENRE VALUES (?,?)";
            List<Genre> uniqueGenre = newFilm.getGenres().stream()
                    .collect(collectingAndThen(toCollection(() ->
                            new TreeSet<>(Comparator.comparingInt(Genre::getId))), ArrayList::new));
            uniqueGenre.forEach(x -> jdbcTemplate.update(sqlInsert, newFilm.getId(), x.getId()));
        }
        return getFilm(newFilm.getId());
    }

    @Override
    public void userLikeFilm(Film film, long id) {
        String sql = "INSERT INTO FILM_LIKES VALUES (?,?)";
        jdbcTemplate.update(sql, film.getId(), id);
    }

    @Override
    public Film userDisLikeFilm(Film film, long id) {
        String sql = "DELETE FROM FILM_LIKES WHERE FILM_ID=? AND USER_ID=?";
        jdbcTemplate.update(sql, film.getId(), id);
        return getFilm(film.getId());
    }

    @Override
    public void deleteFilm(long id) {
        String sql = "DELETE FROM FILMS WHERE id=?";
        jdbcTemplate.update(sql, id);
    }
}

