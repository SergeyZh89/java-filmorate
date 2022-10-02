package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.RecommendationsDao;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class RecommendationsDaoImpl implements RecommendationsDao {

    private final JdbcTemplate jdbcTemplate;
    private final FilmDao filmDao;

    public RecommendationsDaoImpl(JdbcTemplate jdbcTemplate, FilmDao filmDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmDao = filmDao;
    }

    @Override
    public List<Film> getRecommendationsByUser(long userId, int recCount) {
        String sql = "WITH ul AS (\n" +
                "SELECT * \n" +
                "FROM FILM_LIKES fl \n" +
                "WHERE fl.USER_ID = "+ userId +" \n" +
                "),\n" +
                "fu AS (\n" +
                "SELECT fl.USER_ID\n" +
                "FROM FILM_LIKES fl\n" +
                "JOIN ul ON ul.FILM_ID = fl.FILM_ID AND fl.USER_ID <> ul.USER_ID\n" +
                "GROUP BY fl.USER_ID\n" +
                "),\n" +
                "ff AS (\n" +
                "SELECT fl.FILM_ID\n" +
                ", COUNT(*) AS FILM_RANK \n" +
                "FROM FILM_LIKES fl\n" +
                "JOIN fu ON fl.USER_ID = fu.USER_ID\n" +
                "WHERE NOT EXISTS (SELECT 0 FROM ul WHERE ul.FILM_ID = fl.FILM_ID)\n" +
                "GROUP BY fl.FILM_ID\n" +
                ")\n" +
                "SELECT f.ID, f.NAME, f.DESCRIPTION, f.DURATION, f.RELEASE_DATE, f.MPA, MA.NAME\n" +
                "FROM FILMS f\n" +
                "JOIN MPA_RATINGS AS MA ON F.MPA = MA.ID\n" +
                "JOIN ff ON f.ID = ff.FILM_ID\n" +
                "ORDER BY ff.FILM_RANK DESC \n" +
                "LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs),
               recCount);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        return new Film().toBuilder()
                    .id(rs.getInt("id"))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .duration(rs.getInt("duration"))
                    .releaseDate(rs.getDate("release_date").toLocalDate())
                    .mpa(filmDao.ratingMpaMapper(rs.getInt("MPA")))
                    .genres(filmDao.genreMapper(rs.getInt("id")))
                    .userLikes(filmDao.userLikesMapper(rs.getInt("id")))
                    .build();
        }
}
