package ru.yandex.practicum.filmorate.storages.films;

import ru.yandex.practicum.filmorate.models.Film;

import java.util.List;
import java.util.Set;

public interface FilmStorage {
    public List<Film> getFilms();

    public void clearFilms();

    public Film create(Film film, Integer mpaId, Set<Integer> genreIds);

    public Film update(Film film, Integer mpaId, Set<Integer> genreIds);

}
