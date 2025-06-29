package ru.yandex.practicum.filmorate.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

import static ru.yandex.practicum.filmorate.validators.ValidatorsConstants.RELEASE_DATE_MIN_VALUE;

@Data
public class Film {

    private int id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    private LocalDate releaseDate;

    @Min(1)
    private int duration;

    @AssertTrue(message = "Дата релиза фильма должна быть не раньше 28 декабря 1895 года")
    @JsonIgnore
    public boolean isReleaseDateValid() {
        if (releaseDate == null) {
            return true;
        }
        return releaseDate.isAfter(RELEASE_DATE_MIN_VALUE);
    }
}
