package ru.yandex.practicum.filmorate.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.storages.genres.DbGenreStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreService {
    private final DbGenreStorage dbGenreStorage;

    public List<Genre> getGenres() {
        return dbGenreStorage.getGenres();
    }


    public Genre getGenre(Integer genreId) {
        return dbGenreStorage.getGenre(genreId).orElseThrow(() -> new NotFoundException("Жанр с id = " + genreId + " не найден"));
    }
}
