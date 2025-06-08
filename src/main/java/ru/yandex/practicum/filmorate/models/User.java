package ru.yandex.practicum.filmorate.models;

import lombok.Data;

import java.time.LocalDate;

public @Data class User {
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}

