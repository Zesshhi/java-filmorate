package ru.yandex.practicum.filmorate.storages.genres;

import ru.yandex.practicum.filmorate.models.Genre;

import java.util.List;

public interface GenreStorage {
    public List<Genre> getGenres();

    public void clearGenres();

    public Genre create(Genre genre);

    public Genre update(Genre genre);

}
