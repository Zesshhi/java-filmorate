package ru.yandex.practicum.filmorate.storages.mpa;

import ru.yandex.practicum.filmorate.models.Mpa;

import java.util.List;

public interface MpaStorage {
    public List<Mpa> getMpas();

    public void clearMpas();

    public Mpa create(Mpa mpa);

    public Mpa update(Mpa mpa);

}
