package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
    public Optional<Film> getFilm(long id) {
        String sql = "SELECT F.ID, F.NAME, F.DESCRIPTION, F.DURATION, F.RELEASE_DATE, MPA, MA.NAME FROM FILMS AS F " +
                "JOIN MPA_RATINGS AS MA ON F.MPA = MA.ID where F.ID = ?";
        return Optional.ofNullable(jdbcTemplate.query(sql, (rs, rowNum) -> new Film().toBuilder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .duration(rs.getInt("duration"))
                        .releaseDate(rs.getDate("release_date").toLocalDate())
                        .mpa(ratingMpaMapper(rs.getInt("MPA")))
                        .genres(genreMapper(rs.getInt("id")))
                        .userLikes(userLikesMapper(rs.getInt("id")))
                        .build(), id).stream()
                .findAny()
                .orElseThrow(() -> new FilmNotFoundException("Такого фильма не существует")));
    }

    public List<Film> getPopularFilms(int count, Long genreId, Integer year) {
        if (genreId == null & year == null) {
            String sql = "SELECT F.* FROM FILMS AS F " +
                    "LEFT JOIN FILM_LIKES FL on F.ID = FL.FILM_ID " +
                    "GROUP BY F.ID " +
                    "ORDER BY COUNT(FL.FILM_ID) " +
                    "DESC " +
                    "LIMIT ?";
            return jdbcTemplate.query(sql, rs -> {
                List<Film> filmList = new ArrayList<>();
                while (rs.next()) {
                    Film film = mapRowToFilm(rs);
                    filmList.add(film);
                }
                return filmList;
            }, count);
        }

        if (year != null && genreId != null) {
            final String sql_s_genre_year = "SELECT * FROM FILMS AS f " +
                    "LEFT OUTER JOIN (SELECT FILM_ID, COUNT (*) likes_count FROM FILM_LIKES GROUP BY FILM_ID) " +
                    "AS l ON f.ID = l.FILM_ID " +
                    "LEFT OUTER JOIN MPA_RATINGS AS mpa ON f.MPA = mpa.ID " +
                    "LEFT OUTER JOIN FILM_GENRE AS fg ON f.ID = fg.FILM_ID " +
                    "WHERE fg.GENRE_ID = ? AND EXTRACT (YEAR FROM f.RELEASE_DATE) = ? " +
                    "ORDER BY l.likes_count DESC " +
                    "LIMIT ?;";
            return jdbcTemplate.query(sql_s_genre_year, rs -> {
                List<Film> filmList = new ArrayList<>();
                while (rs.next()) {
                    Film film = mapRowToFilm(rs);
                    filmList.add(film);
                }
                return filmList;
            }, genreId, year, count);
        }

        if (year == null && genreId != null) {
            final String sql_s_genre = "SELECT * FROM FILMS AS f " +
                    "LEFT OUTER JOIN (SELECT FILM_ID, COUNT (*) likes_count FROM FILM_LIKES GROUP BY FILM_ID) " +
                    "AS l ON f.ID = l.FILM_ID " +
                    "LEFT OUTER JOIN MPA_RATINGS AS mpa ON f.MPA = mpa.ID " +
                    "LEFT OUTER JOIN FILM_GENRE AS fg ON f.ID = fg.FILM_ID " +
                    "WHERE fg.GENRE_ID = ? " +
                    "ORDER BY l.likes_count DESC " +
                    "LIMIT ?;";
            return jdbcTemplate.query(sql_s_genre, rs -> {
                List<Film> filmList = new ArrayList<>();
                while (rs.next()) {
                    Film film = mapRowToFilm(rs);
                    filmList.add(film);
                }
                return filmList;
            }, genreId, count);
        }

        if (year != null) {
            final String sql_s_year = "SELECT * FROM FILMS AS f " +
                    "LEFT OUTER JOIN (SELECT FILM_ID, COUNT (*) likes_count FROM FILM_LIKES GROUP BY FILM_ID) " +
                    "AS l ON f.ID = l.FILM_ID " +
                    "LEFT OUTER JOIN MPA_RATINGS AS mpa ON f.MPA = mpa.ID " +
                    "WHERE EXTRACT (YEAR FROM f.release_date) = ? " +
                    "ORDER BY l.likes_count DESC " +
                    "LIMIT ?;";
            return jdbcTemplate.query(sql_s_year, rs -> {
                List<Film> filmList = new ArrayList<>();
                while (rs.next()) {
                    Film film = mapRowToFilm(rs);
                    filmList.add(film);
                }
                return filmList;
            }, year, count);
        }

        throw new FilmNotFoundException("Такого фильма не существует");
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        String sql = "SELECT F.* FROM FILMS AS F " +
                "LEFT JOIN FILM_LIKES FL on F.ID = FL.FILM_ID " +
                "LEFT JOIN USERS U ON FL.USER_ID = U.ID " +
                "WHERE USER_ID = ?" +
                "INTERSECT " +
                "SELECT F.* FROM FILMS AS F " +
                "LEFT JOIN FILM_LIKES FL on F.ID = FL.FILM_ID " +
                "LEFT JOIN USERS U ON FL.USER_ID = U.ID " +
                "WHERE USER_ID = ? ";
        return jdbcTemplate.query(sql, rs -> {
            List<Film> filmList = new ArrayList<>();
            while (rs.next()) {
                Film film = mapRowToFilm(rs);
                filmList.add(film);
            }
            return filmList;
        }, userId, friendId);
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

        return getFilm(newFilm.getId()).get();
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
        return getFilm(newFilm.getId()).get();
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
        return getFilm(film.getId()).get();
    }

    @Override
    public void deleteFilm(long id) {
        String sql = "DELETE FROM FILMS WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Film> getPopularFilmsBySearch(String query, List<String> by) {
        if (by.isEmpty()) {
            String sql = "SELECT * FROM FILMS WHERE FILMS.DESCRIPTION LIKE '%?%'";
            return jdbcTemplate.query(sql, rs -> {
                List<Film> filmList = new ArrayList<>();
                while (rs.next()) {
                    Film film = mapRowToFilm(rs);
                    filmList.add(film);
                }
                return filmList;
            }, query, by);
        }
        throw new FilmNotFoundException("Такого фильма не существует");
    }

    private Film mapRowToFilm(ResultSet rs) throws SQLException {
        return new Film().toBuilder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .duration(rs.getInt("duration"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .mpa(ratingMpaMapper(rs.getInt("MPA")))
                .genres(genreMapper(rs.getInt("id")))
                .userLikes(userLikesMapper(rs.getInt("id")))
                .build();
    }
}

