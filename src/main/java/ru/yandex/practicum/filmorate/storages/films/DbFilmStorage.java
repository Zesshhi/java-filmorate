package ru.yandex.practicum.filmorate.storages.films;


import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.storages.BaseRepository;
import ru.yandex.practicum.filmorate.storages.genres.GenreRowMapper;

import java.sql.Date;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@Repository
public class DbFilmStorage extends BaseRepository<Film> implements FilmStorage {


    public DbFilmStorage(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public List<Film> getFilms() {
        String stmt = """
                SELECT f.*,
                       CASE WHEN ARRAY_AGG(DISTINCT m.id)[1] IS NULL THEN NULL ELSE  ARRAY_AGG(DISTINCT (m.id || ':' || m.name))[1] END  as mpa,
                       CASE WHEN ARRAY_AGG(DISTINCT fg.genre_id)[1] IS NULL THEN NULL ELSE ARRAY_AGG(DISTINCT (fg.genre_id || ':' || g.name)) END AS genres
                FROM films f
                         LEFT JOIN film_genres fg ON f.id = fg.film_id
                         LEFT join genres g ON g.id = fg.genre_id
                         LEFT JOIN film_mpa fm ON f.id = fm.film_id
                         LEFT join mpa m ON fm.mpa_id = m.id
                GROUP BY f.id
                """;
        return jdbcTemplate.query(stmt, new FilmRowMapper());
    }

    public Film getFilm(int id) {
        String stmt = """
                SELECT f.*,
                       CASE WHEN ARRAY_AGG(DISTINCT m.id)[1] IS NULL THEN NULL ELSE  ARRAY_AGG(DISTINCT (m.id || ':' || m.name))[1] END  as mpa,
                       CASE WHEN ARRAY_AGG(DISTINCT fg.genre_id)[1] IS NULL THEN NULL ELSE ARRAY_AGG(DISTINCT (fg.genre_id || ':' || g.name)) END AS genres
                FROM films f
                         LEFT JOIN film_genres fg ON f.id = fg.film_id
                         LEFT join genres g ON g.id = fg.genre_id
                         LEFT JOIN film_mpa fm ON f.id = fm.film_id
                         LEFT join mpa m ON fm.mpa_id = m.id
                GROUP BY f.id
                HAVING f.id = ?
                """;
        return jdbcTemplate.queryForObject(stmt, new FilmRowMapper(), id);
    }

    @Override
    public void clearFilms() {
        String stmt = """
                TRUNCATE TABLE films CASCADE
                """;
        jdbcTemplate.update(
                stmt
        );
    }

    @Override
    public Film create(Film film, Integer mpaId, Set<Integer> genreIds) {
        String stmt = """
                INSERT INTO films (name, description, release_date, duration) VALUES (?, ?, ?, ?)
                """;
        int id = insert(
                stmt,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration()
        );
        film.setId(id);

        if (mpaId != null) {
            addMpa(id, mpaId);
        }

        if (!genreIds.isEmpty()) {
            updateGenres(id, genreIds);
        }

        return film;
    }

    private void addGenres(Integer filmId, Set<Integer> genreIds) {
        String stmtToAddGenres = """
                INSERT INTO film_genres (film_id, genre_id)
                VALUES (?, ?)
                """;
        manyInsert(
                stmtToAddGenres,
                filmId,
                genreIds
        );
    }

    private void updateGenres(Integer filmId, Set<Integer> genreIds) {
        String stmtToClearFilmGenres = """
                DELETE
                FROM FILM_GENRES
                WHERE FILM_ID = ?
                """;
        insertInTableConnections(
                stmtToClearFilmGenres,
                filmId
        );
        addGenres(filmId, genreIds);
    }

    private void updateMpa(Integer filmId, Integer mpaId) {
        String stmtToUpdateMpa = """
                MERGE INTO FILM_MPA (film_id, mpa_id) KEY (FILM_ID) VALUES (?, ?)
                """;
        insertInTableConnections(stmtToUpdateMpa, filmId, mpaId);
    }

    private void addMpa(Integer filmId, Integer mpaId) {
        String stmtToAddMpa = """
                INSERT INTO film_mpa (film_id, mpa_id)
                VALUES (?, ?)
                """;
        insertInTableConnections(
                stmtToAddMpa,
                filmId,
                mpaId
        );
    }

    @Override
    public Film update(Film film, Integer mpaId, Set<Integer> genreIds) {
        String stmt = """
                UPDATE films
                SET name = ?, description = ?, release_date = ?, duration = ?
                WHERE id = ?
                """;
        jdbcTemplate.update(
                stmt,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getId()
        );

        if (mpaId != null) {
            updateMpa(film.getId(), mpaId);
        }

        if (!genreIds.isEmpty()) {
            addGenres(film.getId(), genreIds);
        }

        return film;
    }


    public void addUserLikeToFilm(Integer filmId, Integer userId) {
        String stmt = """
                INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)
                """;
        jdbcTemplate.update(stmt, filmId, userId);
    }

    public void deleteUserLikeFromFilm(Integer filmId, Integer userId) {
        String stmt = """
                DELETE FROM film_likes WHERE film_id = ? and user_id = ?
                """;

        jdbcTemplate.update(stmt, filmId, userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        String stmt = """
                SELECT *
                from (
                        SELECT f.*,
                               CASE WHEN ARRAY_AGG(DISTINCT m.id)[1] IS NULL THEN NULL ELSE  ARRAY_AGG(DISTINCT (m.id || ':' || m.name))[1] END  as mpa,
                               CASE WHEN ARRAY_AGG(DISTINCT fg.genre_id)[1] IS NULL THEN NULL ELSE ARRAY_AGG(DISTINCT (fg.genre_id || ':' || g.name)) END AS genres
                        FROM films f
                                 LEFT JOIN film_genres fg ON f.id = fg.film_id
                                 LEFT join genres g ON g.id = fg.genre_id
                                 LEFT JOIN film_mpa fm ON f.id = fm.film_id
                                 LEFT join mpa m ON fm.mpa_id = m.id
                        GROUP BY f.id
                        ) f
                         join (select count(user_id) as amount_of_likes, film_id
                               from FILM_LIKES
                               group by film_id) fl on f.id = fl.film_id
                order by fl.amount_of_likes DESC
                LIMIT ?
                """;

        return jdbcTemplate.query(stmt, new FilmRowMapper(), count);
    }


    public void addGenreToFilm(Integer filmId, Integer genreId) {
        String stmt = """
                INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)
                """;
        jdbcTemplate.update(stmt, filmId, genreId);
    }

    public void deleteGenreFromFilm(Integer filmId, Integer genreId) {
        String stmt = """
                DELETE FROM film_genres WHERE film_id = ? and genre_id = ?
                """;

        jdbcTemplate.update(stmt, filmId, genreId);
    }

    public List<Genre> getFilmGenres(Integer filmId) {
        String stmt = """
                SELECT *
                from genres g
                         join (select genre_id
                               from film_genres) fg on g.id = fg.genre_id
                """;

        return jdbcTemplate.query(stmt, new GenreRowMapper());
    }

}
