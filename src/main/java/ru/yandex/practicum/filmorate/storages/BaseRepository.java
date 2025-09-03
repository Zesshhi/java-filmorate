package ru.yandex.practicum.filmorate.storages;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class BaseRepository<T> {

    public final JdbcTemplate jdbcTemplate;
    public final RowMapper<T> rowMapper;

    protected GeneratedKeyHolder generateInsert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection
                            .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                    for (int idx = 0; idx < params.length; idx++) {
                        ps.setObject(idx + 1, params[idx]);
                    }
                    return ps;
                }, keyHolder
        );

        return keyHolder;
    }

    protected int insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = generateInsert(query, params);

        int id = Objects.requireNonNull(keyHolder.getKeyAs(Long.class)).intValue();

        return id;
    }

    protected void insertInTableConnections(String query, Object... params) {
        generateInsert(query, params);
    }

    protected void manyInsert(String query, Integer relatedObjectId, Set<Integer> objectIds) {
        List<Object[]> batchArgs = objectIds.stream()
                .map(genreId -> new Object[]{relatedObjectId, genreId})
                .toList();
        jdbcTemplate.batchUpdate(query, batchArgs);
    }

    protected Optional<T> getOne(String query, Object... params) {
        try {
            T result = jdbcTemplate.queryForObject(query, rowMapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    protected List<T> getAll(String query, Object... params) {
        return jdbcTemplate.query(query, rowMapper, params);
    }

}
