package ru.yandex.practicum.filmorate.models;

import lombok.Data;

import java.time.LocalDate;

public @Data class Film {

    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;

}
