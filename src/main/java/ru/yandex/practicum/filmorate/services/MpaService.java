package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Mpa;
import ru.yandex.practicum.filmorate.storages.mpa.MpaStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage dbMpaStorage;

    public List<Mpa> getMpas() {
        return dbMpaStorage.getMpas();
    }

    public Mpa getMpa(Integer mpaId) {
        return dbMpaStorage.getMpa(mpaId).orElseThrow(() -> new NotFoundException("Mpa с id = " + mpaId + " не найден"));
    }
}
