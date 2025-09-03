package ru.yandex.practicum.filmorate;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.services.MpaService;
import ru.yandex.practicum.filmorate.storages.mpa.DbMpaStorage;
import ru.yandex.practicum.filmorate.storages.mpa.MpaRowMapper;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;


@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({
        MpaService.class,
        DbMpaStorage.class,
        MpaRowMapper.class
})
@Sql(scripts = "/setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class MpaControllerTests {

    @Autowired
    private MpaService mpaService;

    @Test
    public void should_return_mpa() throws Exception {
        assertEquals(1, mpaService.getMpa(1).getId());
    }

    @Test
    public void should_not_return_mpa() throws Exception {
        assertThatThrownBy(() -> mpaService.getMpa(666)).isInstanceOf(NotFoundException.class);
    }

    @Test
    public void should_return_mpas() throws Exception {
        assertEquals(5, mpaService.getMpas().size());
    }
}