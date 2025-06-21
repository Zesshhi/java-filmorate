package ru.yandex.practicum.filmorate.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class User {

    private int id;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Pattern(regexp = "\\S+")
    private String login;

    private String name;

    @Past
    private LocalDate birthday;
}

