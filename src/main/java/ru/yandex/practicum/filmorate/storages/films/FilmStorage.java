package ru.yandex.practicum.filmorate.storages.films;

import ru.yandex.practicum.filmorate.models.Film;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {
    public List<Film> getFilms();

    public List<Film> getPopularFilms(Integer count);

    public Optional<Film> getFilm(int id);

    public void clearFilms();

    public Film create(Film film, Integer mpaId, Set<Integer> genreIds);

    public Film update(Film film, Integer mpaId, Set<Integer> genreIds);

    public void addUserLikeToFilm(Integer filmId, Integer userId);

    public void deleteUserLikeFromFilm(Integer filmId, Integer userId);

}
