package ru.yandex.practicum.filmorate;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.Mpa;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.services.DbFilmService;
import ru.yandex.practicum.filmorate.services.DbGenreService;
import ru.yandex.practicum.filmorate.services.DbMpaService;
import ru.yandex.practicum.filmorate.services.DbUserService;
import ru.yandex.practicum.filmorate.storages.films.DbFilmStorage;
import ru.yandex.practicum.filmorate.storages.genres.DbGenreStorage;
import ru.yandex.practicum.filmorate.storages.mpa.DbMpaStorage;
import ru.yandex.practicum.filmorate.storages.users.DbUserStorage;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;


@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({
        DbFilmService.class,
        DbFilmStorage.class,
        DbGenreService.class,
        DbGenreStorage.class,
        DbMpaService.class,
        DbMpaStorage.class,
        DbUserService.class,
        DbUserStorage.class
})
@Sql(scripts = "/setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class FilmControllerTests {

    @Autowired
    private DbFilmService dbFilmService;

    @Autowired
    private DbGenreService dbGenreService;

    @Autowired
    private DbMpaService dbMpaService;

    @Autowired
    private DbUserService dbUserService;

    private Film film2 = new Film(2, "film Name 2", "film Name Description 2", "2000-01-01", 100);


    @Test
    public void should_create_film() throws Exception {
        dbFilmService.create(film2);

        Film newFilm = dbFilmService.getFilm(2);
        assertThat(newFilm.getId()).isEqualTo(film2.getId());
    }

    @Test
    public void should_create_film_with_mpa() throws Exception {
        Mpa mpa1 = dbMpaService.getMpa(1);
        film2.setMpa(mpa1);

        dbFilmService.create(film2);

        Film newFilm = dbFilmService.getFilm(2);

        assertEquals(1, newFilm.getMpa().getId());
    }

    @Test
    public void should_create_film_with_genres() throws Exception {
        Genre genre1 = dbGenreService.getGenre(1);
        Genre genre2 = dbGenreService.getGenre(2);

        List<Genre> genres = Arrays.asList(genre1, genre2);
        film2.setGenres(genres);

        dbFilmService.create(film2);

        Film newFilm = dbFilmService.getFilm(2);

        assertEquals(2, newFilm.getGenres().size());
    }

    @Test
    public void should_update_film() throws Exception {
        dbFilmService.create(film2);

        film2.setName("Aboba Film Update");
        film2.setDescription("Aboba Film Update Description");
        film2.setReleaseDate(LocalDate.parse("2001-08-20"));
        film2.setDuration(50);

        dbFilmService.update(film2);

        Film updatedFilm = dbFilmService.getFilm(2);

        assertThat(updatedFilm).hasFieldOrPropertyWithValue("name", "Aboba Film Update");
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("description", "Aboba Film Update Description");
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("releaseDate", LocalDate.parse("2001-08-20"));
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("duration", 50);
    }

    @Test
    public void should_not_update_film() throws Exception {
        dbFilmService.create(film2);

        film2.setId(666);
        film2.setName("Aboba Film Update");
        film2.setDescription("Aboba Film Update Description");
        film2.setReleaseDate(LocalDate.parse("2001-08-20"));
        film2.setDuration(50);

        assertThatThrownBy(() -> dbFilmService.update(film2)).isInstanceOf(NotFoundException.class);
    }

    @Test
    public void should_return_films() throws Exception {
        dbFilmService.create(film2);

        assertEquals(2, dbFilmService.getFilms().size());
    }

    @Test
    public void should_add_like_to_film() throws Exception {
        dbFilmService.create(film2);

        dbFilmService.addLikeToFilm(film2.getId(), 1);
    }

    @Test
    public void should_remove_film_like() throws Exception {
        dbFilmService.create(film2);

        dbFilmService.addLikeToFilm(film2.getId(), 1);
        dbFilmService.deleteLikeFromFilm(film2.getId(), 1);
    }

    @Test
    public void should_return_popular_films() throws Exception {
        User user2 = new User(2, "aboba2@mail.ru", "abobaLogin2", "aboba user3", "1946-08-20");
        User user3 = new User(3, "aboba3@mail.ru", "abobaLogin3", "aboba user4", "1946-08-20");
        User user4 = new User(4, "aboba4@mail.ru", "abobaLogin4", "aboba User4", "1946-08-20");

        Film film3 = new Film(3, "film Name 3", "film Name Description 3", "2001-01-01", 60);

        dbUserService.create(user2);
        dbUserService.create(user3);
        dbUserService.create(user4);

        dbFilmService.create(film2);
        dbFilmService.create(film3);

        dbFilmService.addLikeToFilm(film2.getId(), 1);
        dbFilmService.addLikeToFilm(film2.getId(), 2);
        dbFilmService.addLikeToFilm(film2.getId(), 3);
        dbFilmService.addLikeToFilm(film3.getId(), 1);
        dbFilmService.addLikeToFilm(film3.getId(), 4);
        dbFilmService.addLikeToFilm(1, 1);

        assertEquals(2, dbFilmService.getPopularFilms(10).get(0).getId());
        assertEquals(3, dbFilmService.getPopularFilms(10).get(1).getId());
        assertEquals(1, dbFilmService.getPopularFilms(10).get(2).getId());

    }

}