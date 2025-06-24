package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.models.Film;

import java.util.ArrayList;

public interface FilmStorage {
    public ArrayList<Film> getFilms();

    public void clearFilms();

    public Film create(Film film);

    public Film update(Film film);

}
