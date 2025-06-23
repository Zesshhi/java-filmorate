package ru.yandex.practicum.filmorate.storages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.Film;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final Map<Integer, Set<Integer>> filmsUsersLikes = new HashMap<>();


    public ArrayList<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    public ArrayList<Integer> getFilmsIds() {
        return new ArrayList<>(films.keySet());
    }

    public void clearFilms() {
        films.clear();
    }

    public Film create(Film film) {
        film.setId(getNextId());

        films.put(film.getId(), film);
        return film;
    }

    public Film update(Film newfilm) {
        films.replace(newfilm.getId(), newfilm);
        return newfilm;
    }

    public Map<Integer, Set<Integer>> getFilmUsersLikes() {
        return filmsUsersLikes;
    }

    public void addUserLikeToFilm(Integer filmId, Integer userId) {
        addFilmToUsersMapIfNotExist(filmId);

        Set<Integer> filmsLikes = filmsUsersLikes.get(filmId);

        if (filmsLikes == null) {
            filmsLikes = new HashSet<>();
            filmsLikes.add(userId);
            filmsUsersLikes.put(filmId, filmsLikes);
        } else {
            filmsLikes.add(userId);
        }
    }


    public void deleteFilmLikeFromUser(Set<Integer> filmsLikes, Integer filmId, Integer userId) {
        addFilmToUsersMapIfNotExist(filmId);

        filmsLikes.remove(userId);
    }

    public ArrayList<Film> getPopularFilms(Set<Integer> filmsIds) {

        ArrayList<Film> popularFilms = new ArrayList<>();

        for (int filmId : filmsIds) {

            addFilmToUsersMapIfNotExist(filmId);

            popularFilms.add(films.get(filmId));
        }

        return popularFilms;
    }

    public Set<Integer> getFilmLikes(Integer filmId) {
        return filmsUsersLikes.getOrDefault(filmId, new HashSet<>());
    }

    private void addFilmToUsersMapIfNotExist(Integer filmId) {
        if (!filmsUsersLikes.containsKey(filmId)) {
            filmsUsersLikes.put(filmId, new HashSet<>());
        }
    }

    private int getNextId() {
        int currentMaxId = (int) films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
