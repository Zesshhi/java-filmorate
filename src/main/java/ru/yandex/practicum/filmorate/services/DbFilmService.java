package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.Mpa;
import ru.yandex.practicum.filmorate.storages.films.DbFilmStorage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DbFilmService {

    private final DbFilmStorage dbFilmStorage;
    private final DbUserService dbUserService;
    private final DbGenreService dbGenreService;
    private final DbMpaService dbMpaService;

    public List<Film> getFilms() {
        return dbFilmStorage.getFilms();
    }

    public Film getFilm(int id) {
        return validateUnknownFilm(id);
    }


    public void clearFilms() {
        dbFilmStorage.clearFilms();
    }

    private Integer getMpaIdForFilm(Film film) {
        Integer mpaId = Optional.ofNullable(film.getMpa()).map(Mpa::getId).orElse(null);
        validateUnknownMpa(mpaId);
        return mpaId;
    }

    private Set<Integer> getGenresIdsForFilm(Film film) {
        Set<Integer> genreIds = Optional.ofNullable(
                film.getGenres()
        ).map(
                genres -> genres.stream().map(Genre::getId).collect(Collectors.toSet())
        ).orElse(Collections.emptySet());
        validateUnknownGenres(genreIds);

        return genreIds;
    }

    public Film create(Film film) {
        Integer mpaId = getMpaIdForFilm(film);

        Set<Integer> genreIds = getGenresIdsForFilm(film);

        return dbFilmStorage.create(film, mpaId, genreIds);
    }

    public Film update(Film newfilm) {
        validateUnknownFilm(newfilm.getId());

        Integer mpaId = getMpaIdForFilm(newfilm);

        Set<Integer> genreIds = getGenresIdsForFilm(newfilm);

        return dbFilmStorage.update(newfilm, mpaId, genreIds);
    }

    public void addLikeToFilm(Integer filmId, Integer userId) {
        validateUnknownFilm(filmId);

        dbFilmStorage.addUserLikeToFilm(filmId, userId);
    }


    public void deleteLikeFromFilm(Integer filmId, Integer userId) {
        validateUnknownFilm(filmId);
        dbUserService.validateUnkownUser(userId);


        dbFilmStorage.deleteUserLikeFromFilm(filmId, userId);
    }


    public List<Film> getPopularFilms(Integer count) {
        return dbFilmStorage.getPopularFilms(count);

    }

    private Film validateUnknownFilm(Integer filmId) {
        try {
            return dbFilmStorage.getFilm(filmId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
    }

    private void validateUnknownMpa(Integer mpaId) {
        if (mpaId != null) {
            dbMpaService.validateUnknownMpa(mpaId);
        }

    }

    private void validateUnknownGenres(Set<Integer> genreIds) {
        if (!genreIds.isEmpty()) {
            for (Integer genreId : genreIds) {
                dbGenreService.validateUnknownGenre(genreId);
            }
        }
    }


}
