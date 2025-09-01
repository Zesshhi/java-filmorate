package ru.yandex.practicum.filmorate.storages.genres;

import ru.yandex.practicum.filmorate.models.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    public List<Genre> getGenres();

    public Optional<Genre> getGenre(int id);

    public void clearGenres();

    public Genre create(Genre genre);

    public Genre update(Genre genre);

}
