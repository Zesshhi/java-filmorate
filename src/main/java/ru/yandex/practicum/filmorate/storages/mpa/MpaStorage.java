package ru.yandex.practicum.filmorate.storages.mpa;

import ru.yandex.practicum.filmorate.models.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaStorage {
    public List<Mpa> getMpas();

    public Optional<Mpa> getMpa(int id);

    public void clearMpas();

    public Mpa create(Mpa mpa);

    public Mpa update(Mpa mpa);

}
