package ru.yandex.practicum.filmorate.controllers;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@Slf4j
@NoArgsConstructor
@RequestMapping("/genres")
public class GenresController {
    private GenreService genreService;

    @Autowired
    public GenresController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping()
    public List<Genre> genresList() {
        log.debug("Получен запрос на список жанров");
        return genreService.getGenres();
    }

    @GetMapping("/{id}")
    public Genre genre(@PathVariable int id) {
        log.debug("Получен запрос на жанр c id = " + id);
        return genreService.getGenresById(id);
    }
}
