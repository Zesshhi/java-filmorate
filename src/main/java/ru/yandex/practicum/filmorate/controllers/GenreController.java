package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.services.GenreService;

import java.util.List;

@RestController
@RequestMapping("genres")
@Slf4j
@RequiredArgsConstructor
public class GenreController {
    private final GenreService dbGenreService;

    @GetMapping
    public List<Genre> getGenres() {
        return dbGenreService.getGenres();
    }

    @GetMapping("/{id}")
    public Genre getGenre(@PathVariable int id) {
        return dbGenreService.getGenre(id);
    }
}
