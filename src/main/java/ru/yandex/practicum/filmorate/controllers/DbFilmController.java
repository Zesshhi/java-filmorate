package ru.yandex.practicum.filmorate.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.services.DbFilmService;

import java.util.List;

@RestController
@RequestMapping("films")
@Slf4j
@RequiredArgsConstructor
public class DbFilmController {

    private final DbFilmService dbFilmService;

    @GetMapping
    public List<Film> getFilms() {
        return dbFilmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) {
        return dbFilmService.getFilm(id);
    }

    public void clearFilms() {
        dbFilmService.clearFilms();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return dbFilmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newfilm) {
        return dbFilmService.update(newfilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToFilm(@PathVariable int id, @PathVariable int userId) {
        dbFilmService.addLikeToFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteFilmLike(@PathVariable int id, @PathVariable int userId) {
        dbFilmService.deleteLikeFromFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") Integer count) {
        return dbFilmService.getPopularFilms(count);
    }

}
