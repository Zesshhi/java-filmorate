package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.Mpa;
import ru.yandex.practicum.filmorate.storages.BaseRepository;
import ru.yandex.practicum.filmorate.storages.films.DbFilmStorage;
import ru.yandex.practicum.filmorate.storages.films.FilmStorage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage dbFilmStorage;
    private final UserService userService;
    private final GenreService dbGenreService;
    private final MpaService mpaService;

    public List<Film> getFilms() {
        return dbFilmStorage.getFilms();
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
        getFilm(newfilm.getId());

        Integer mpaId = getMpaIdForFilm(newfilm);

        Set<Integer> genreIds = getGenresIdsForFilm(newfilm);

        return dbFilmStorage.update(newfilm, mpaId, genreIds);
    }

    public void addLikeToFilm(Integer filmId, Integer userId) {
        getFilm(filmId);

        dbFilmStorage.addUserLikeToFilm(filmId, userId);
    }


    public void deleteLikeFromFilm(Integer filmId, Integer userId) {
        getFilm(filmId);
        userService.getUser(userId);


        dbFilmStorage.deleteUserLikeFromFilm(filmId, userId);
    }


    public List<Film> getPopularFilms(Integer count) {
        return dbFilmStorage.getPopularFilms(count);

    }

    public Film getFilm(Integer filmId) {
        return dbFilmStorage.getFilm(filmId).orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
    }

    private void validateUnknownMpa(Integer mpaId) {
        if (mpaId != null) {
            mpaService.getMpa(mpaId);
        }

    }

    private void validateUnknownGenres(Set<Integer> genreIds) {
        if (!genreIds.isEmpty()) {
            for (Integer genreId : genreIds) {
                dbGenreService.getGenre(genreId);
            }
        }
    }


}
