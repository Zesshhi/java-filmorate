package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.services.GenreService;
import ru.yandex.practicum.filmorate.storages.genres.DbGenreStorage;
import ru.yandex.practicum.filmorate.storages.genres.GenreRowMapper;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({
        GenreService.class,
        DbGenreStorage.class,
        GenreRowMapper.class
})
@Sql(scripts = "/setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class GenreControllerTests {

    @Autowired
    private GenreService dbGenreService;


    @Test
    public void should_return_genre() throws Exception {
        assertEquals(1, dbGenreService.getGenre(1).getId());
    }

    @Test
    public void should_not_return_genre() throws Exception {
        assertThatThrownBy(() -> dbGenreService.getGenre(666)).isInstanceOf(NotFoundException.class);
    }

    @Test
    public void should_return_genres() throws Exception {
        assertEquals(6, dbGenreService.getGenres().size());
    }
}