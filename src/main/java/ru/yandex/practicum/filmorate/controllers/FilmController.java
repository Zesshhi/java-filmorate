package ru.yandex.practicum.filmorate.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.services.FilmService;

import java.util.ArrayList;

@RestController
@RequestMapping("films")
@Slf4j
public class FilmController {

    @Autowired
    private FilmService filmService;

    @GetMapping
    public ArrayList<Film> getFilms() {
        return filmService.getFilms();
    }

    public void clearFilms() {
        filmService.clearFilms();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newfilm) {
        return filmService.update(newfilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addFilmLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLikeToFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteFilmLike(@PathVariable int id, @PathVariable int userId) {
        filmService.deleteLikeFromFilm(id, userId);
    }

    @GetMapping("/popular")
    public ArrayList<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") Integer count) {
        return filmService.getPopularFilms(count);
    }


}
