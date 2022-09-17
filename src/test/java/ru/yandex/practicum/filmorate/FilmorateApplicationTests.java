package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dao.impl.FilmDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.RatingMpaDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.UserDaoImpl;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    private final UserDaoImpl userDao;
    private final FilmDaoImpl filmDao;
    private final RatingMpaDaoImpl ratingMpaDao;
    private final GenreDaoImpl genreDao;


    @Test
    void testAddUserShouldNameTom() {
        User user = new User();
        user.setName("Tom");
        user.setLogin("TomLogin");
        user.setEmail("tom@mail.ru");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userDao.addUser(user);
        assertThat(userDao.getUser(1))
                .isPresent()
                .hasValueSatisfying(userFind -> assertThat(userFind).hasFieldOrPropertyWithValue("name", "Tom"));

    }

    @Test
    void testFindUserByIdShouldNameBob() {
        User user = new User();
        user.setName("Bob");
        user.setLogin("TomLogin");
        user.setEmail("tom@mail.ru");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userDao.addUser(user);
        Optional<User> userOptional = userDao.getUser(1);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(userFind ->
                        assertThat(userFind).hasFieldOrPropertyWithValue("name", "Bob"));
    }

    @Test
    void testGetUsersShouldSize2() {
        User user = new User();
        user.setName("Tom");
        user.setLogin("TomLogin");
        user.setEmail("tom@mail.ru");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userDao.addUser(user);
        userDao.addUser(user);
        assertThat(userDao.getUsers()).hasSize(2);
    }

    @Test
    void testGetFriendsShouldSize2() {
        User user = new User();
        user.setName("Tom");
        user.setLogin("TomLogin");
        user.setEmail("tom@mail.ru");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userDao.addUser(user);
        User otherUser = new User();
        otherUser.setName("Bob");
        otherUser.setLogin("BobLogin");
        otherUser.setEmail("Bob@mail.ru");
        otherUser.setBirthday(LocalDate.of(1991, 1, 1));
        userDao.addUser(otherUser);
        userDao.addFriend(user, otherUser);
        User someUser = new User();
        someUser.setName("Mike");
        someUser.setLogin("MikeLogin");
        someUser.setEmail("Mike@mail.ru");
        someUser.setBirthday(LocalDate.of(1991, 1, 1));
        userDao.addUser(someUser);
        userDao.addFriend(user, someUser);
        assertThat(userDao.getFriends(1)).hasSize(2);
    }

    @Test
    void testAddFriendShouldSize1() {
        User user = new User();
        user.setName("Tom");
        user.setLogin("TomLogin");
        user.setEmail("tom@mail.ru");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userDao.addUser(user);
        User otherUser = new User();
        otherUser.setName("Bob");
        otherUser.setLogin("BobLogin");
        otherUser.setEmail("Bob@mail.ru");
        otherUser.setBirthday(LocalDate.of(1991, 1, 1));
        userDao.addUser(otherUser);
        userDao.addFriend(user, otherUser);
        assertThat(userDao.getFriends(1)).hasSize(1);
    }

    @Test
    void testGetCommonFriendsShouldId3() {
        User user = new User();
        user.setName("Tom");
        user.setLogin("TomLogin");
        user.setEmail("tom@mail.ru");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userDao.addUser(user);
        User otherUser = new User();
        otherUser.setName("Bob");
        otherUser.setLogin("BobLogin");
        otherUser.setEmail("Bob@mail.ru");
        otherUser.setBirthday(LocalDate.of(1991, 1, 1));
        userDao.addUser(otherUser);
        User someUser = new User();
        someUser.setName("Mike");
        someUser.setLogin("MikeLogin");
        someUser.setEmail("Mike@mail.ru");
        someUser.setBirthday(LocalDate.of(1991, 1, 1));
        userDao.addUser(someUser);
        userDao.addFriend(user, someUser);
        userDao.addFriend(otherUser, someUser);
        assertThat(userDao.getCommonFriends(1, 2).stream().findFirst().get().getId()).isEqualTo(3);
    }

    @Test
    void testDeleteFriendShouldIsEmpty() {
        User user = new User();
        user.setName("Tom");
        user.setLogin("TomLogin");
        user.setEmail("tom@mail.ru");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userDao.addUser(user);
        User otherUser = new User();
        otherUser.setName("Bob");
        otherUser.setLogin("BobLogin");
        otherUser.setEmail("Bob@mail.ru");
        otherUser.setBirthday(LocalDate.of(1991, 1, 1));
        userDao.addUser(otherUser);
        userDao.addFriend(user, otherUser);
        assertThat(userDao.getFriends(1)).hasSize(1);
        userDao.deleteFriend(user, otherUser);
        assertThat(userDao.getFriends(1)).isEmpty();
    }

    @Test
    void testUpdateUserShouldNameBob() {
        User user = new User();
        user.setName("Tom");
        user.setLogin("TomLogin");
        user.setEmail("tom@mail.ru");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userDao.addUser(user);
        user.setName("Bob");
        userDao.updateUser(user);
        assertThat(userDao.getUser(1).get().getName()).isEqualTo("Bob");
    }

    @Test
    void testGetFilmsShouldSize2() {
        Film film = new Film();
        film.setName("Film");
        film.setDuration(150);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setMpa(new RatingMpa(1, null));
        filmDao.createFilm(film);
        Film otherFilm = new Film();
        otherFilm.setName("OtherFilm");
        otherFilm.setDuration(150);
        otherFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        otherFilm.setMpa(new RatingMpa(2, null));
        filmDao.createFilm(otherFilm);
        assertThat(filmDao.getFilms()).hasSize(2);
    }

    @Test
    void testGetFilmShouldTitleFilm() {
        Film film = new Film();
        film.setName("Film");
        film.setDuration(150);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setMpa(new RatingMpa(1, null));
        filmDao.createFilm(film);
        assertThat(filmDao.getFilm(1).getName()).isEqualTo("Film");
    }

    @Test
    void testGetPopularFilmsShouldSize2() {
        Film film = new Film();
        film.setName("Film");
        film.setDuration(150);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setMpa(new RatingMpa(1, null));
        filmDao.createFilm(film);
        Film otherFilm = new Film();
        otherFilm.setName("OtherFilm");
        otherFilm.setDuration(150);
        otherFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        otherFilm.setMpa(new RatingMpa(1, null));
        filmDao.createFilm(film);
        assertThat(filmDao.getPopularFilms(2)).hasSize(2);
    }

    @Test
    void testCreateFilmShouldTitleFilm() {
        Film film = new Film();
        film.setName("Film");
        film.setDuration(150);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setMpa(new RatingMpa(1, null));
        filmDao.createFilm(film);
        assertThat(filmDao.getFilm(1).getName()).isEqualTo("Film");
    }

    @Test
    void testUpdateFilmShouldTitleOtherFilm() {
        Film film = new Film();
        film.setName("Film");
        film.setDuration(150);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setMpa(new RatingMpa(1, null));
        filmDao.createFilm(film);
        film.setName("OtherFilm");
        filmDao.updateFilm(film);
        assertThat(filmDao.getFilm(1).getName()).isEqualTo("OtherFilm");
    }

    @Test
    void testUserLikeFilmShouldCount1() {
        User user = new User();
        user.setName("Tom");
        user.setLogin("TomLogin");
        user.setEmail("tom@mail.ru");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userDao.addUser(user);
        Film film = new Film();
        film.setName("Film");
        film.setDuration(150);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setMpa(new RatingMpa(1, null));
        filmDao.createFilm(film);
        filmDao.userLikeFilm(filmDao.getFilm(1), 1);
        assertThat(filmDao.getFilm(1).getUserLikes()).hasSize(1);
    }

    @Test
    void testUserDisLikeFilmShouldIsEmpty() {
        User user = new User();
        user.setName("Tom");
        user.setLogin("TomLogin");
        user.setEmail("tom@mail.ru");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userDao.addUser(user);
        Film film = new Film();
        film.setName("Film");
        film.setDuration(150);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setMpa(new RatingMpa(1, null));
        filmDao.createFilm(film);
        filmDao.userLikeFilm(filmDao.getFilm(1), 1);
        filmDao.userDisLikeFilm(filmDao.getFilm(1), 1);
        assertThat(filmDao.getFilm(1).getUserLikes()).isEmpty();
    }

    @Test
    void testGetAllRatingsCount7() {
        assertThat(ratingMpaDao.getRatings()).hasSize(5);
    }

    @Test
    void getRatingsById() {
        assertThat(ratingMpaDao.getRatingsById(1)).hasFieldOrPropertyWithValue("name", "G");
        assertThat(ratingMpaDao.getRatingsById(2)).hasFieldOrPropertyWithValue("name", "PG");
        assertThat(ratingMpaDao.getRatingsById(3)).hasFieldOrPropertyWithValue("name", "PG-13");
        assertThat(ratingMpaDao.getRatingsById(4)).hasFieldOrPropertyWithValue("name", "R");
        assertThat(ratingMpaDao.getRatingsById(5)).hasFieldOrPropertyWithValue("name", "NC-17");
    }

    @Test
    void getGenres() {
        assertThat(genreDao.getGenres()).hasSize(6);
    }

    @Test
    void getGenresById() {
        assertThat(genreDao.getGenresById(1)).hasFieldOrPropertyWithValue("name", "Комедия");
        assertThat(genreDao.getGenresById(2)).hasFieldOrPropertyWithValue("name", "Драма");
        assertThat(genreDao.getGenresById(3)).hasFieldOrPropertyWithValue("name", "Мультфильм");
        assertThat(genreDao.getGenresById(4)).hasFieldOrPropertyWithValue("name", "Триллер");
        assertThat(genreDao.getGenresById(5)).hasFieldOrPropertyWithValue("name", "Документальный");
        assertThat(genreDao.getGenresById(6)).hasFieldOrPropertyWithValue("name", "Боевик");
    }
}

