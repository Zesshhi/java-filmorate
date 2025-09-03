package ru.yandex.practicum.filmorate.models;

import lombok.Data;

@Data
public class Entity {

    private Integer id;

    public Entity(Integer id) {
        this.id = id;
    }

    public Entity() {
    }
}
