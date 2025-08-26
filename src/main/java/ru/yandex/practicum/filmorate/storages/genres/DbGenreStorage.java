package ru.yandex.practicum.filmorate.storages.genres;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.storages.BaseRepository;

import java.util.List;


@Slf4j
@Component
@Repository
public class DbGenreStorage extends BaseRepository<Genre> implements GenreStorage {


    public DbGenreStorage(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public List<Genre> getGenres() {
        String stmt = """
                SELECT *
                FROM genres
                """;
        return jdbcTemplate.query(stmt, new GenreRowMapper());
    }

    public Genre getGenre(int id) {
        String stmt = """
                SELECT *
                FROM genres
                WHERE id = ?
                """;
        return jdbcTemplate.queryForObject(stmt, new GenreRowMapper(), id);
    }

    @Override
    public void clearGenres() {
        String stmt = """
                TRUNCATE TABLE genres CASCADE
                """;
        jdbcTemplate.update(
                stmt
        );
    }

    @Override
    public Genre create(Genre genre) {
        String stmt = """
                INSERT INTO genres (name) VALUES (?)
                """;
        int id = insert(
                stmt,
                genre.getName()
        );
        genre.setId(id);

        return genre;
    }

    @Override
    public Genre update(Genre genre) {
        String stmt = """
                UPDATE genres
                SET name = ?
                WHERE id = ?
                """;
        jdbcTemplate.update(
                stmt,
                genre.getName(),
                genre.getId()
        );
        return genre;
    }

}
