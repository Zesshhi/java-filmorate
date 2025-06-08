package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {

        validateFilmData(film);

        film.setId(getNextId());

        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newfilm) {

        if (films.containsKey(newfilm.getId())) {

            validateFilmData(newfilm);

            films.replace(newfilm.getId(), newfilm);
            return newfilm;
        }
        throw new NotFoundException("Фильм с id = " + newfilm.getId() + " не найден");
    }


    private int getNextId() {
        int currentMaxId = (int) films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }


    public void validateFilmData(Film film) throws ConditionsNotMetException {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название фильма должно быть указано");
            throw new ConditionsNotMetException("Название фильма должно быть указано");
        }

        if (film.getDescription().length() > 200) {
            log.error("Максимальная длина описания 200 символов");
            throw new ConditionsNotMetException("Максимальная длина описания 200 символов");
        }

        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            log.error("Дата релиза фильма раньше чем 1895-12-28");
            throw new ConditionsNotMetException("Дата релиза фильма раньше чем 1895-12-28");
        }

        if (film.getDuration() < 1) {
            log.error("Продолжительность фильма должна быть положительным числом");
            throw new ConditionsNotMetException("Продолжительность фильма должна быть положительным числом");
        }

    }

}
