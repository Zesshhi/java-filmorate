package ru.yandex.practicum.filmorate.storages.genres;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.storages.BaseRepository;

import java.util.List;
import java.util.Optional;


@Slf4j
@Component
@Repository
public class DbGenreStorage extends BaseRepository<Genre> implements GenreStorage {


    public DbGenreStorage(JdbcTemplate jdbcTemplate, GenreRowMapper rowMapper) {
        super(jdbcTemplate, rowMapper);
    }

    @Override
    public List<Genre> getGenres() {
        String stmt = """
                SELECT *
                FROM genres
                """;
        return this.getAll(stmt);
    }

    public Optional<Genre> getGenre(int id) {
        String stmt = """
                SELECT *
                FROM genres
                WHERE id = ?
                """;
        return this.getOne(stmt, id);
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
