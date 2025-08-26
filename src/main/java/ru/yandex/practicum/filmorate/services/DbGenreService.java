package ru.yandex.practicum.filmorate.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.storages.genres.DbGenreStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DbGenreService {
    private final DbGenreStorage dbGenreStorage;

    public List<Genre> getGenres() {
        return dbGenreStorage.getGenres();
    }

    public Genre getGenre(int id) {
        return validateUnknownGenre(id);
    }

    public Genre validateUnknownGenre(Integer genreId) {
        try {
            return dbGenreStorage.getGenre(genreId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Жанр с id = " + genreId + " не найден");
        }
    }
}
