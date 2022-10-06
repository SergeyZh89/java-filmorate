package ru.yandex.practicum.filmorate.controllers;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/directors")
@NoArgsConstructor
public class DirectorController {
    private DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    List<Director> getDirectors() {
        log.debug("Получен запрос на список режиссеров");
        return directorService.getDirectors();
    }

    @GetMapping("/{id}")
    Director getDirectorById(@PathVariable long id) {
        log.debug("Получен запрос режиссера с id " + id);
        return directorService.getDirectorById(id);
    }

    @PostMapping
    Director createDirector(@RequestBody Director director) {
        log.debug("Получен запрос на создание режиссера");
        return directorService.createDirector(director);
    }

    @PutMapping
    Director updateDirector(@RequestBody Director director) {
        log.debug("Получен запрос на обновление режиссера с id " + director.getId());
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    void deleteDirector(@PathVariable long id) {
        log.debug("Получен запрос на удаление режиссера с id " + id);
        directorService.deleteDirector(id);
    }
}
