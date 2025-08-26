package ru.yandex.practicum.filmorate.storages.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.models.Mpa;
import ru.yandex.practicum.filmorate.storages.BaseRepository;

import java.util.List;


@Slf4j
@Component
@Repository
public class DbMpaStorage extends BaseRepository<Mpa> implements MpaStorage {


    public DbMpaStorage(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public List<Mpa> getMpas() {
        String stmt = """
                SELECT *
                FROM mpa
                """;
        return jdbcTemplate.query(stmt, new MpaRowMapper());
    }

    public Mpa getMpa(int id) {
        String stmt = """
                SELECT *
                FROM mpa
                WHERE id = ?
                """;
        return jdbcTemplate.queryForObject(stmt, new MpaRowMapper(), id);
    }

    @Override
    public void clearMpas() {
        String stmt = """
                TRUNCATE TABLE mpa CASCADE
                """;
        jdbcTemplate.update(
                stmt
        );
    }

    @Override
    public Mpa create(Mpa mpa) {
        String stmt = """
                INSERT INTO mpa (name) VALUES (?)
                """;
        int id = insert(
                stmt,
                mpa.getName()
        );
        mpa.setId(id);

        return mpa;
    }

    @Override
    public Mpa update(Mpa mpa) {
        String stmt = """
                UPDATE mpa
                SET name = ?
                WHERE id = ?
                """;
        jdbcTemplate.update(
                stmt,
                mpa.getName(),
                mpa.getId()
        );
        return mpa;
    }

}
