package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Mpa;
import ru.yandex.practicum.filmorate.storages.mpa.DbMpaStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DbMpaService {
    private final DbMpaStorage dbMpaStorage;

    public List<Mpa> getMpas() {
        return dbMpaStorage.getMpas();
    }

    public Mpa getMpa(int id) {
        return validateUnknownMpa(id);
    }

    public Mpa validateUnknownMpa(Integer mpaId) {
        try {
            return dbMpaStorage.getMpa(mpaId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Mpa с id = " + mpaId + " не найден");
        }
    }
}
