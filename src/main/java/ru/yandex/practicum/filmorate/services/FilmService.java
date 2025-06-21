package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.storages.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storages.InMemoryUserStorage;

import java.util.*;

import static java.util.stream.Collectors.toMap;

@Service
@Slf4j
public class FilmService {
    @Autowired
    private InMemoryFilmStorage inMemoryFilmStorage;
    @Autowired
    private InMemoryUserStorage inMemoryUserStorage;


    public ArrayList<Film> getFilms() {
        return inMemoryFilmStorage.getFilms();
    }

    public void clearFilms() {
        inMemoryFilmStorage.clearFilms();
    }

    public Film create(Film film) {
        return inMemoryFilmStorage.create(film);
    }

    public Film update(Film newfilm) {

        if (!inMemoryFilmStorage.getFilmsIds().contains(newfilm.getId())) {
            throw new NotFoundException("Фильм с id = " + newfilm.getId() + " не найден");
        }

        return inMemoryFilmStorage.update(newfilm);
    }

    public void addLikeToFilm(Integer filmId, Integer userId) {
        validateFilmsLikes(filmId, userId);

        inMemoryFilmStorage.addUserLikeToFilm(filmId, userId);
    }


    public void deleteLikeFromFilm(Integer filmId, Integer userId) {
        validateFilmsLikes(filmId, userId);

        Set<Integer> filmsLikes = inMemoryFilmStorage.getFilmUsersLikes().getOrDefault(filmId, new HashSet<>());

        inMemoryFilmStorage.deleteFilmLikeFromUser(filmsLikes, filmId, userId);
    }


    public ArrayList<Film> getPopularFilms(Integer count) {
        Map<Integer, Set<Integer>> filmsIds = inMemoryFilmStorage.getFilmUsersLikes()
                .entrySet()
                .stream()
                .sorted(Comparator.comparingInt(e -> -e.getValue().size()))
                .limit(count)
                .collect(toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (entry1, entry2) -> entry1,
                        LinkedHashMap::new
                ));

        return inMemoryFilmStorage.getPopularFilms(filmsIds.keySet());

    }


    private void validateFilmsLikes(Integer filmId, Integer userId) {
        if (!inMemoryFilmStorage.getFilmsIds().contains(filmId)) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }

        if (!inMemoryUserStorage.getUsersIds().contains(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }


}
