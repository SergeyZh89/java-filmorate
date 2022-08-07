package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    FilmController controller;

    @BeforeEach
    void create() {
        controller = new FilmController();
    }

    @Test
    void shouldFalseFilmNameIsBlank() {
        Film film = new Film(1, "", "description", LocalDate.of(2000, 1, 1), 100);
        assertThrows(ValidationException.class,() ->controller.validatorFilm(film));
    }

    @Test
    void shouldTrueFilmName() {
        Film film = new Film(1, "name", "description", LocalDate.of(2000, 1, 1), 100);
        assertDoesNotThrow(() ->controller.validatorFilm(film));
    }

    @Test
    void shouldTrueFilmDescription199() {
        Film film = new Film(1, "name", "Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «св", LocalDate.of(2000, 1, 1), 100);
        assertDoesNotThrow(() ->controller.validatorFilm(film));
    }

    @Test
    void shouldTrueFilmDescription200() {
        Film film = new Film(1, "name", "Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «сво", LocalDate.of(2000, 1, 1), 100);
        assertDoesNotThrow(() ->controller.validatorFilm(film));
    }

    @Test
    void shouldFalseFilmDescription201() {
        Film film = new Film(1, "name", "Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «свое", LocalDate.of(2000, 1, 1), 100);
        assertThrows(ValidationException.class,() ->controller.validatorFilm(film));
    }

    @Test
    void shouldFalseFilmReleaseDateBefore1895year12month28day() {
        Film film = new Film(1, "name", "description", LocalDate.of(1895, 12, 27), 100);
        assertThrows(ValidationException.class,() ->controller.validatorFilm(film));
    }

    @Test
    void shouldTrueFilmReleaseDate1895year12month28day() {
        Film film = new Film(1, "name", "description", LocalDate.of(1895, 12, 28), 100);
        assertDoesNotThrow(() ->controller.validatorFilm(film));
    }

    @Test
    void shouldTrueFilmReleaseDateAfter1895year12month28day() {
        Film film = new Film(1, "name", "description", LocalDate.of(1895, 12, 29), 100);
        assertDoesNotThrow(() ->controller.validatorFilm(film));
    }

    @Test
    void shouldFalseFilmDurationNegative() {
        Film film = new Film(1, "name", "description", LocalDate.of(1895, 12, 29), -1);
        assertThrows(ValidationException.class,() ->controller.validatorFilm(film));
    }

    @Test
    void shouldFalseFilmDurationZero() {
        Film film = new Film(1, "name", "description", LocalDate.of(1895, 12, 29), 0);
        assertThrows(ValidationException.class,() ->controller.validatorFilm(film));
    }

    @Test
    void shouldTrueFilmDurationPozitive() {
        Film film = new Film(1, "name", "description", LocalDate.of(1895, 12, 29), 1);
        assertDoesNotThrow(() ->controller.validatorFilm(film));
    }
}