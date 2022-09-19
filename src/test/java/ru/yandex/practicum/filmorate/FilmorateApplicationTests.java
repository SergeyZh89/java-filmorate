package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.RatingMpaService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    private final UserService userService;
    private final FilmService filmService;
    private final RatingMpaService ratingMpaService;
    private final GenreService genreService;


    @Test
    void testAddUserShouldNameTom() {
        User user = new User().toBuilder()
                .name("Tom")
                .login("TomLogin")
                .email("tom@mail.ru")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        userService.addUser(user);
        assertThat(userService.getUser(1))
                .isPresent()
                .hasValueSatisfying(userFind -> assertThat(userFind).hasFieldOrPropertyWithValue("name", "Tom"));

    }

    @Test
    void testFindUserByIdShouldNameBob() {
        User user = new User().toBuilder()
                .name("Bob")
                .login("BobLogin")
                .email("bob@mail.ru")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        userService.addUser(user);
        Optional<User> userOptional = userService.getUser(1);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(userFind ->
                        assertThat(userFind).hasFieldOrPropertyWithValue("name", "Bob"));
    }

    @Test
    void testGetUsersShouldSize2() {
        User user = new User().toBuilder()
                .name("Tom")
                .login("TomLogin")
                .email("tom@mail.ru")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        userService.addUser(user);
        userService.addUser(user);
        assertThat(userService.getUsers()).hasSize(2);
    }

    @Test
    void testGetFriendsShouldSize2() {
        User user = new User().toBuilder()
                .name("Tom")
                .login("TomLogin")
                .email("tom@mail.ru")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        userService.addUser(user);
        User otherUser = new User().toBuilder()
                .name("Bob")
                .login("BobLogin")
                .email("bob@mail.ru")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        userService.addUser(otherUser);
        userService.addFriend(user, otherUser);
        User someUser = new User().toBuilder()
                .name("Mike")
                .login("MikeLogin")
                .email("mike@mail.ru")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        someUser.setBirthday(LocalDate.of(1991, 1, 1));
        userService.addUser(someUser);
        userService.addFriend(user, someUser);
        assertThat(userService.getFriends(1)).hasSize(2);
    }

    @Test
    void testAddFriendShouldSize1() {
        User user = new User().toBuilder()
                .name("Tom")
                .login("TomLogin")
                .email("tom@mail.ru")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        userService.addUser(user);
        User otherUser = new User().toBuilder()
                .name("Bob")
                .login("BobLogin")
                .email("bob@mail.ru")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        userService.addUser(otherUser);
        userService.addFriend(user, otherUser);
        assertThat(userService.getFriends(1)).hasSize(1);
    }

    @Test
    void testGetCommonFriendsShouldId3() {
        User user = new User().toBuilder()
                .name("Tom")
                .login("TomLogin")
                .email("tom@mail.ru")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        userService.addUser(user);
        User otherUser = new User().toBuilder()
                .name("Bob")
                .login("BobLogin")
                .email("bob@mail.ru")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        userService.addUser(otherUser);
        User someUser = new User().toBuilder()
                .name("Mike")
                .login("MikeLogin")
                .email("mike@mail.ru")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        userService.addUser(someUser);
        userService.addFriend(user, someUser);
        userService.addFriend(otherUser, someUser);
        assertThat(userService.getCommonFriends(1, 2).stream().findFirst().get().getId()).isEqualTo(3);
    }

    @Test
    void testDeleteFriendShouldIsEmpty() {
        User user = new User().toBuilder()
                .name("Tom")
                .login("TomLogin")
                .email("tom@mail.ru")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        userService.addUser(user);
        User otherUser = new User().toBuilder()
                .name("Bob")
                .login("BobLogin")
                .email("bob@mail.ru")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        userService.addUser(otherUser);
        userService.addFriend(user, otherUser);
        assertThat(userService.getFriends(1)).hasSize(1);
        userService.deleteFriend(user, otherUser);
        assertThat(userService.getFriends(1)).isEmpty();
    }

    @Test
    void testUpdateUserShouldNameBob() {
        User user = new User().toBuilder()
                .name("Tom")
                .login("TomLogin")
                .email("tom@mail.ru")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        userService.addUser(user);
        user.setName("Bob");
        userService.updateUser(user);
        assertThat(userService.getUser(1).get().getName()).isEqualTo("Bob");
    }

    @Test
    void testGetFilmsShouldSize2() {
        Film film = new Film().toBuilder()
                .name("Film")
                .duration(150)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .mpa(new RatingMpa(1, null))
                .build();
        filmService.createFilm(film);
        Film otherFilm = new Film().toBuilder()
                .name("OtherFilm")
                .duration(150)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .mpa(new RatingMpa(2, null))
                .build();
        filmService.createFilm(otherFilm);
        assertThat(filmService.getFilms()).hasSize(2);
    }

    @Test
    void testGetFilmShouldTitleFilm() {
        Film film = new Film().toBuilder()
                .name("Film")
                .duration(150)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .mpa(new RatingMpa(1, null))
                .build();
        filmService.createFilm(film);
        assertThat(filmService.getFilm(1).getName()).isEqualTo("Film");
    }

    @Test
    void testGetPopularFilmsShouldSize2() {
        Film film = new Film().toBuilder()
                .name("Film")
                .duration(150)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .mpa(new RatingMpa(1, null))
                .build();
        filmService.createFilm(film);
        Film otherFilm = new Film().toBuilder()
                .name("OtherFilm")
                .duration(150)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .mpa(new RatingMpa(2, null))
                .build();
        filmService.createFilm(otherFilm);
        assertThat(filmService.getPopularFilms(2)).hasSize(2);
    }

    @Test
    void testCreateFilmShouldTitleFilm() {
        Film film = new Film().toBuilder()
                .name("Film")
                .duration(150)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .mpa(new RatingMpa(1, null))
                .build();
        filmService.createFilm(film);
        assertThat(filmService.getFilm(1).getName()).isEqualTo("Film");
    }

    @Test
    void testUpdateFilmShouldTitleOtherFilm() {
        Film film = new Film().toBuilder()
                .name("Film")
                .duration(150)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .mpa(new RatingMpa(1, null))
                .build();
        filmService.createFilm(film);
        film.setName("OtherFilm");
        filmService.updateFilm(film);
        assertThat(filmService.getFilm(1).getName()).isEqualTo("OtherFilm");
    }

    @Test
    void testUserLikeFilmShouldCount1() {
        User user = new User().toBuilder()
                .name("Tom")
                .login("TomLogin")
                .email("tom@mail.ru")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        userService.addUser(user);
        Film film = new Film().toBuilder()
                .name("Film")
                .duration(150)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .mpa(new RatingMpa(1, null))
                .build();
        filmService.createFilm(film);
        filmService.userLikeFilm(filmService.getFilm(1), 1);
        assertThat(filmService.getFilm(1).getUserLikes()).hasSize(1);
    }

    @Test
    void testUserDisLikeFilmShouldIsEmpty() {
        User user = new User().toBuilder()
                .name("Tom")
                .login("TomLogin")
                .email("tom@mail.ru")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        userService.addUser(user);
        Film film = new Film().toBuilder()
                .name("Film")
                .duration(150)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .mpa(new RatingMpa(1, null))
                .build();
        filmService.createFilm(film);
        filmService.userLikeFilm(filmService.getFilm(1), 1);
        filmService.userDisLikeFilm(filmService.getFilm(1), 1);
        assertThat(filmService.getFilm(1).getUserLikes()).isEmpty();
    }

    @Test
    void testGetAllRatingsCount7() {
        assertThat(ratingMpaService.getRatings()).hasSize(5);
    }

    @Test
    void testGetRatingsById() {
        assertThat(ratingMpaService.getRatingsById(1)).hasFieldOrPropertyWithValue("name", "G");
        assertThat(ratingMpaService.getRatingsById(2)).hasFieldOrPropertyWithValue("name", "PG");
        assertThat(ratingMpaService.getRatingsById(3)).hasFieldOrPropertyWithValue("name", "PG-13");
        assertThat(ratingMpaService.getRatingsById(4)).hasFieldOrPropertyWithValue("name", "R");
        assertThat(ratingMpaService.getRatingsById(5)).hasFieldOrPropertyWithValue("name", "NC-17");
    }

    @Test
    void testGetGenresCount6() {
        assertThat(genreService.getGenres()).hasSize(6);
    }

    @Test
    void testGetGenresById() {
        assertThat(genreService.getGenresById(1)).hasFieldOrPropertyWithValue("name", "Комедия");
        assertThat(genreService.getGenresById(2)).hasFieldOrPropertyWithValue("name", "Драма");
        assertThat(genreService.getGenresById(3)).hasFieldOrPropertyWithValue("name", "Мультфильм");
        assertThat(genreService.getGenresById(4)).hasFieldOrPropertyWithValue("name", "Триллер");
        assertThat(genreService.getGenresById(5)).hasFieldOrPropertyWithValue("name", "Документальный");
        assertThat(genreService.getGenresById(6)).hasFieldOrPropertyWithValue("name", "Боевик");
    }

    @Test
    void testDeleteFilm() {
        Film film = new Film().toBuilder()
                .name("Film")
                .duration(150)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .mpa(new RatingMpa(1, null))
                .build();
        filmService.createFilm(film);
        assertThat(filmService.getFilm(1).getName()).isEqualTo("Film");
        assertThat(filmService.getFilms()).hasSize(1);
        filmService.deleteFilm(1);
        assertThat(filmService.getFilms()).isEmpty();
    }
}
