package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FeedService;

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
    FeedService feedService;

    @Autowired
    public FilmDaoImpl(JdbcTemplate jdbcTemplate, FeedService feedService) {
        this.jdbcTemplate = jdbcTemplate;
        this.feedService = feedService;
    }

    public List<Genre> genreMapper(int id) {
        String sql = "SELECT G.NAME, G.ID FROM GENRES AS G JOIN FILM_GENRE AS FG ON G.ID = FG.GENRE_ID WHERE FILM_ID=?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Genre.class), id);
    }

    public List<Long> userLikesMapper(int id) {
        String sql = "SELECT USER_ID FROM FILM_LIKES WHERE FILM_ID=?";
        return jdbcTemplate.queryForList(sql, Long.TYPE, id);
    }

    public RatingMpa ratingMpaMapper(int id) {
        String sql = "SELECT * FROM MPA_RATINGS WHERE ID=?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(RatingMpa.class), id).stream()
                .findFirst()
                .orElse(null);
    }


    private List<Director> directorMapper(int id) {
        String sql = "SELECT D.ID, D.NAME FROM DIRECTOR AS D " +
                "JOIN FILM_DIRECTOR AS FD ON D.ID = FD.DIRECTOR_ID WHERE FILM_ID=?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Director.class), id);
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
                        .directors(directorMapper(rs.getInt("id")))
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
                        .directors(directorMapper(rs.getInt("id")))
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
                    .forEach(x -> jdbcTemplate.update("INSERT INTO FILM_GENRE VALUES (?,?)", newFilm.getId(),
                            x.getId()));
        }
        if (newFilm.getDirectors() != null) {
            newFilm.getDirectors().forEach(x -> jdbcTemplate.update("INSERT INTO FILM_DIRECTOR VALUES (?,?)",
                    newFilm.getId(), x.getId()));
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
        String sqlDeleteDirector = "DELETE FROM FILM_DIRECTOR WHERE FILM_ID=?";
        jdbcTemplate.update(sqlDeleteDirector, newFilm.getId());
        if (!newFilm.getGenres().isEmpty()) {
            String sqlInsert = "INSERT INTO FILM_GENRE VALUES (?,?)";
            List<Genre> uniqueGenre = newFilm.getGenres().stream()
                    .collect(collectingAndThen(toCollection(() ->
                            new TreeSet<>(Comparator.comparingInt(Genre::getId))), ArrayList::new));
            uniqueGenre.forEach(x -> jdbcTemplate.update(sqlInsert, newFilm.getId(), x.getId()));
        }
        if (!newFilm.getDirectors().isEmpty()) {
            String sqlInsert = "INSERT INTO FILM_DIRECTOR VALUES (?,?)";
            List<Director> directors = newFilm.getDirectors().stream()
                    .collect(collectingAndThen(toCollection(() ->
                            new TreeSet<>(Comparator.comparingLong(Director::getId))), ArrayList::new));
            directors.forEach(x -> jdbcTemplate.update(sqlInsert, newFilm.getId(), x.getId()));
        }
        return getFilm(newFilm.getId()).get();
    }

    public List<Film> sortFilmsDirector(int directorId, String sortBy) {
        if (directorMapper(directorId).isEmpty()) {
            log.error("Режиссёр с таким id не существует");
            throw new DirectorNotFoundException("Режиссёр с таким id не существует");
        }
        if (Objects.equals(sortBy, "year")) {
            String sql = "SELECT F.ID, F.NAME, F.DESCRIPTION, F.DURATION, F.RELEASE_DATE, MPA, MA.NAME " +
                    "FROM FILMS AS F " +
                    "JOIN MPA_RATINGS AS MA ON F.MPA = MA.ID " +
                    "JOIN FILM_DIRECTOR AS FD ON F.ID = FD.FILM_ID " +
                    "WHERE FD.DIRECTOR_ID = ? " +
                    "ORDER BY F.RELEASE_DATE ";
            return jdbcTemplate.query(sql, rs -> {
                List<Film> filmList = new ArrayList<>();
                while (rs.next()) {
                    mapRowToFilm(rs);
                    Film film = new Film().toBuilder()
                            .id(rs.getInt("id"))
                            .name(rs.getString("name"))
                            .description(rs.getString("description"))
                            .duration(rs.getInt("duration"))
                            .releaseDate(rs.getDate("release_date").toLocalDate())
                            .mpa(ratingMpaMapper(rs.getInt("MPA")))
                            .genres(genreMapper(rs.getInt("id")))
                            .userLikes(userLikesMapper(rs.getInt("id")))
                            .directors(directorMapper(rs.getInt("id")))
                            .build();
                    filmList.add(film);
                }
                return filmList;

            }, directorId);
        }
        if (Objects.equals(sortBy, "likes")) {
            String sql = "SELECT F.ID, F.NAME, F.DESCRIPTION, F.DURATION, F.RELEASE_DATE, MPA, MA.NAME " +
                    "FROM FILMS AS F " +
                    "JOIN MPA_RATINGS AS MA ON F.MPA = MA.ID " +
                    "JOIN FILM_DIRECTOR AS FD ON F.ID = FD.FILM_ID " +
                    "WHERE FD.DIRECTOR_ID = ? ";
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
                            .directors(directorMapper(rs.getInt("id")))
                            .build();
                    filmList.add(film);
                }
                filmList.forEach(o1 -> {
                    o1.getUserLikes().sort(Comparator.naturalOrder());
                });
                return filmList;

            }, directorId);

        }
        return null;
    }

    @Override
    public void userLikeFilm(long filmId, long userId) {
        feedService.addEvent(new Event(System.currentTimeMillis(), userId, "LIKE", "ADD",
                0L, filmId));
        String sql = "MERGE INTO FILM_LIKES VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public Film userDisLikeFilm(long filmId, long userId) {
        String sql = "DELETE FROM FILM_LIKES WHERE FILM_ID=? AND USER_ID=?";
        jdbcTemplate.update(sql, filmId, userId);
        feedService.addEvent(new Event(System.currentTimeMillis(), userId, "LIKE", "REMOVE",
                0L, filmId));
        return getFilm(filmId).get();
    }

    @Override
    public void deleteFilm(long id) {
        String sql = "DELETE FROM FILMS WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Film> getPopularFilmsBySearch(String query, List<String> by) {
        if ((query == null)) {
            String sql = "SELECT F.* FROM FILMS AS F " +
                    "LEFT JOIN FILM_LIKES FL on F.ID = FL.FILM_ID " +
                    "GROUP BY F.ID " +
                    "ORDER BY COUNT(FL.FILM_ID) " +
                    "DESC";
            return Optional.ofNullable(jdbcTemplate.query(sql, rs -> {
                List<Film> filmList = new ArrayList<>();
                while (rs.next()) {
                    Film film = mapRowToFilm(rs);
                    filmList.add(film);
                }
                return filmList;
            })).orElseThrow(() -> new FilmNotFoundException("Фильмов не найдено"));
        }
        if (by.size() == 1) {
            String search = by.get(0);
            if ("title".equals(search)) {
                String sqlTitle = "SELECT * FROM FILMS AS F\n" +
                        "         LEFT JOIN FILM_LIKES FL on F.ID = FL.FILM_ID\n" +
                        "WHERE LOWER(NAME) LIKE LOWER(?)\n" +
                        "GROUP BY F.ID\n" +
                        "ORDER BY COUNT(FL.FILM_ID)\n" +
                        "DESC";
                return jdbcTemplate.query(sqlTitle, rs -> {
                    List<Film> filmList = new ArrayList<>();
                    while (rs.next()) {
                        Film film = mapRowToFilm(rs);
                        filmList.add(film);
                    }
                    return filmList;
                }, String.format("%%%s%%", query));
            } else if ("director".equals(search)) {
                String sqlDirector = "SELECT F.ID, F.NAME, F.DESCRIPTION, D.NAME, F.DURATION, F.RELEASE_DATE, F.MPA\n" +
                        "FROM FILMS AS F\n" +
                        "         LEFT JOIN FILM_LIKES FL on F.ID = FL.FILM_ID\n" +
                        "         LEFT JOIN FILM_DIRECTOR FD on F.ID = FD.FILM_ID\n" +
                        "         LEFT JOIN DIRECTOR D on FD.DIRECTOR_ID = D.ID\n" +
                        "WHERE LOWER(D.NAME) LIKE LOWER(?)\n" +
                        "GROUP BY F.ID\n" +
                        "ORDER BY COUNT(FL.FILM_ID)\n" +
                        "DESC";
                return jdbcTemplate.query(sqlDirector, rs -> {
                    List<Film> filmList = new ArrayList<>();
                    while (rs.next()) {
                        Film film = mapRowToFilm(rs);
                        filmList.add(film);
                    }
                    return filmList;
                }, String.format("%%%s%%", query));
            }
        } else if (by.size() == 2) {
            String sqlFilms = "SELECT F.ID, F.NAME, F.DESCRIPTION, D.NAME, F.DURATION, F.RELEASE_DATE, F.MPA\n" +
                    "FROM FILMS AS F\n" +
                    "         LEFT JOIN FILM_DIRECTOR FD on F.ID = FD.FILM_ID\n" +
                    "         LEFT JOIN DIRECTOR D on FD.DIRECTOR_ID = D.ID\n" +
                    "         LEFT JOIN FILM_LIKES FL on F.ID = FL.FILM_ID\n" +
                    "WHERE LOWER(F.NAME) LIKE LOWER(?)\n" +
                    "   OR LOWER(D.NAME) LIKE LOWER(?)\n" +
                    "GROUP BY F.ID\n" +
                    "ORDER BY COUNT(FL.FILM_ID)\n" +
                    "DESC";
            return jdbcTemplate.query(sqlFilms, rs -> {
                List<Film> filmList = new ArrayList<>();
                while (rs.next()) {
                    Film film = mapRowToFilm(rs);
                    filmList.add(film);
                }
                return filmList;
            }, String.format("%%%s%%", query), String.format("%%%s%%", query));
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
                .directors(directorMapper(rs.getInt("id")))
                .build();
    }
}
