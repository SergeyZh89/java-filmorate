package ru.yandex.practicum.filmorate.controllers;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.service.RatingMpaService;

import java.util.List;

@RestController
@Slf4j
@NoArgsConstructor
@RequestMapping("/mpa")
public class RatingMpaController {
    private RatingMpaService ratingMpaService;

    @Autowired
    public RatingMpaController(RatingMpaService ratingMpaService) {
        this.ratingMpaService = ratingMpaService;
    }

    @GetMapping()
    public List<RatingMpa> ratingMpas() {
        log.debug("Получен запрос на список рейтингов");
        return ratingMpaService.getRatings();
    }

    @GetMapping("/{id}")
    public RatingMpa ratingMpaById(@PathVariable int id) {
        log.debug("Получен запрос на рейтингом c id = " + id);
        return ratingMpaService.getRatingsById(id);
    }
}
